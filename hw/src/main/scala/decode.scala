package TurboRav

import Chisel._
import Constants._

class Decode extends Module {

  require(Config.xlen == 32 || Config.xlen == 64 || Config.xlen == 128)

  // all shift codes end in 01
  private def isShift(func3: Bits) = (!func3(1) && func3(0))

  private def isJump(opcode: Bits) = {
    opcode === OPCODE_JAL ||
    opcode === OPCODE_JALR
  }

  private def isLongJump(opcode: Bits) = {
    opcode === OPCODE_JAL
  }

  private def isUpper(opcode: Bits) = {
    opcode === OPCODE_AUIPC ||
    opcode === OPCODE_LUI
  }

  private def isMemop(opcode: Bits) = {
    opcode === OPCODE_LOAD ||
    opcode === OPCODE_STORE
  }

  private def isLoad(opcode: Bits) = {
    opcode === OPCODE_LOAD
  }

  private def isStore(opcode: Bits) = {
    opcode === OPCODE_STORE
  }

  private def isBranch(opcdoe: Bits) = {
    opcode === OPCODE_BRANCH
  }

  private def isMultop(opcode: Bits) = {
    opcode === OPCODE_MULT_DIV
  }

  private def isRegop(opcode: Bits) = {
    opcode === OPCODE_REG_IMM ||
    opcode === OPCODE_REG_REG
  }

  private def isRegImm(opcode: Bits) = {
    opcode === OPCODE_REG_IMM
  }

  private def isRegReg(opcode: Bits) = {
    opcode === OPCODE_REG_REG
  }

  val io = new DecodeIO()

  val fch_dec = Reg(init = new FetchDecode())

  val flushed_pipeline = new FetchDecode()

  when(io.hdu_dec.stall){
    // Create a bubble when stalling.
    io.dec_exe.kill()
    // TODO: The bubble should result in a NOP, bru_func should have
    // the value BNOT.
  }

  // Default to writing the input values to the pipeline register
  // fch_dec. But when flushing, the pipeline register should be
  // cleared, and when stalling the inputs should be ignored and the
  // pipeline register should not be updated
  fch_dec := MuxCase(
    io.fch_dec,
    Array(
      io.hdu_dec.flush -> flushed_pipeline,
      io.hdu_dec.stall -> fch_dec
    )
  )

  val rs1_addr   = fch_dec.instr(19, 15)
  val rs2_addr   = fch_dec.instr(24, 20)

  val rd_addr    = fch_dec.instr(11, 7)

  val opcode     = fch_dec.instr(6, 0)
  val func3      = fch_dec.instr(14, 12)
  val func7      = fch_dec.instr(31, 25)
  val alu_func_r = Cat(fch_dec.instr(30), func3)
  val alu_func_i = Cat(UInt(0, width = 1), func3)

  //Sign extended immediates
  val imm_i = Cat(Fill(fch_dec.instr(31), Config.xlen - 12),
                  fch_dec.instr(31, 20))

  val imm_s = Cat(Fill(fch_dec.instr(31), Config.xlen - 12),
                  fch_dec.instr(31, 25),
                  rd_addr)

  val imm_b = Cat(Fill(fch_dec.instr(31), Config.xlen - 12),
                  fch_dec.instr(7),
                  fch_dec.instr(30, 25),
                  fch_dec.instr(11, 8),
                  UInt(0, width = 1))

  val imm_j = Cat(Fill(fch_dec.instr(31), Config.xlen - 20),
                  fch_dec.instr(19, 12),
                  fch_dec.instr(20),
                  fch_dec.instr(30, 21),
                  UInt(0, width = 1))

  val imm_u32 = Cat(fch_dec.instr(31, 12),
                    UInt(0, width = Config.xlen - 20))
  val imm_u = if (Config.xlen == 32) imm_u32
              else SignExtend(imm_u32, Config.xlen)

  val shamt = Cat(UInt(0, width = Config.xlen - 5),
                  fch_dec.instr(24, 20))

  val regbank = Module(new RegBank())
  regbank.io.rs1_addr := rs1_addr
  regbank.io.rs2_addr := rs2_addr
  io.wrb_dec <> regbank.io

  val dec_exe = io.dec_exe
  val exe_ctrl = dec_exe.exe_ctrl
  val mem_ctrl = dec_exe.mem_ctrl
  val wrb_ctrl = dec_exe.wrb_ctrl

  exe_ctrl.alu_in_a_sel := Mux(opcode === OPCODE_BRANCH ||
                               opcode === OPCODE_JAL    ||
                               opcode === OPCODE_AUIPC,   ALU_IN_A_PC,
                           Mux(opcode === OPCODE_LUI,     ALU_IN_A_ZERO,
                                                          ALU_IN_A_RS1))

  exe_ctrl.alu_in_b_sel := Mux(isRegReg(opcode), ALU_IN_B_RS2, ALU_IN_B_IMM)

  exe_ctrl.alu_func := Mux(isRegImm(opcode) &&
                           !isShift(func3),     alu_func_i,
                       Mux(isJump(opcode)   ||
                           isUpper(opcode)  ||
                           isBranch(opcode) ||
                           isMemop(opcode),     ALU_ADD,
                                                alu_func_r))

  exe_ctrl.bru_func:= Mux(isBranch(opcode), func3,
                      Mux(isJump(opcode),   BJMP,
                                            BNOT  ))

  exe_ctrl.mult_func   := func3
  exe_ctrl.mult_enable := isMultop(opcode) && func7 === Bits("b0000001")

  dec_exe.imm := MuxCase( imm_i, Array(
            (isRegImm(opcode) && isShift(func3)) -> shamt,
            (isUpper(opcode))                    -> imm_u,
            (isStore(opcode))                    -> imm_s,
            (isBranch(opcode))                   -> imm_b,
            (isLongJump(opcode))                 -> imm_j
            ))

  dec_exe.pc       := fch_dec.pc
  dec_exe.rs1_addr := rs1_addr
  dec_exe.rs1      := regbank.io.rs1_data
  dec_exe.rs2_addr := rs2_addr
  dec_exe.rs2      := regbank.io.rs2_data
  dec_exe.rd_addr  := rd_addr

  val is_halfword = isLoad(opcode) && ( func3(0))
  val is_byte     = isLoad(opcode) && (!func3(1) && !func3(0))
  val sign_extend = isLoad(opcode) && (!func3(2))

  dec_exe.mem_ctrl.is_halfword := is_halfword
  dec_exe.mem_ctrl.is_byte     := is_byte

  dec_exe.wrb_ctrl.is_halfword := is_halfword
  dec_exe.wrb_ctrl.is_byte     := is_byte
  dec_exe.wrb_ctrl.sign_extend := sign_extend
  dec_exe.wrb_ctrl.has_wait_state := Bool(false)

  dec_exe.mem_ctrl.write := isStore(opcode)
  dec_exe.mem_ctrl.read  := isLoad(opcode)

  dec_exe.wrb_ctrl.rd_wen :=
    rd_addr =/= UInt(0) && Any(
      isLoad(opcode),
      isRegop(opcode),
      isUpper(opcode),
      isJump(opcode)
  )

  dec_exe.wrb_ctrl.rd_sel :=
    MuxCase(
      RD_ALU,
      Array(
        isJump(opcode) -> RD_PC,
        isLoad(opcode) -> RD_MEM
      )
    )
}
