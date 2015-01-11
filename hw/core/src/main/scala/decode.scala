package TurboRav

import Chisel._
import Common._
import Constants._

class Decode() extends Module {

  require(Config.xlen == 32 || Config.xlen == 64 || Config.xlen == 128)

  // all shift codes end in 01
  def is_shift(func3: Bits) = (!func3(1) && func3(0))

  def is_jump(opcode: Bits) = {
    opcode === OPCODE_JAL ||
    opcode === OPCODE_JALR
  }

  def is_upper(opcode: Bits) = {
    opcode === OPCODE_AUIPC ||
    opcode === OPCODE_LUI
  }

  val io = new DecodeIO()

  val fch_dec = Reg(init = new FetchDecode())
  unless(io.i_stall){
    fch_dec := io.fch_dec
  }

  val rs1_addr = fch_dec.instr(19, 15)
  val rs2_addr = fch_dec.instr(24, 20)

  val opcode     = fch_dec.instr(6, 0)
  val func3 = fch_dec.instr(14, 12)
  val alu_func_r = Cat(fch_dec.instr(30), func3)
  val alu_func_i = Cat(UInt(0, width = 1), func3)

  //Sign extended immediates
  val imm_i = Cat(Fill(fch_dec.instr(31), Config.xlen - 12),
                  fch_dec.instr(31, 20))

  val imm_s = Cat(Fill(fch_dec.instr(31), Config.xlen - 12),
                  fch_dec.instr(31, 25),
                  fch_dec.instr(11, 7))

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
  val imm_u = if(Config.xlen != 32)
              Cat(Fill(imm_u32(31), Config.xlen - 32), imm_u32)
              else imm_u32

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

  exe_ctrl.alu_in_b_sel := Mux(opcode === OPCODE_REG_REG,
                               ALU_IN_B_RS2,
                               ALU_IN_B_IMM)

  exe_ctrl.alu_func:= Mux(opcode === OPCODE_REG_IMM &&
                         !is_shift(func3),                     alu_func_i,
                      Mux(is_jump(opcode) || is_upper(opcode), ALU_ADD,
                                                               alu_func_r))

  exe_ctrl.bru_func:= Mux(opcode === OPCODE_BRANCH, func3,
                      Mux(is_jump(opcode),          BJMP,
                                                    BNOT))

  dec_exe.imm := MuxCase( imm_i, Array(
            (opcode === OPCODE_REG_IMM && is_shift(func3))      -> shamt,
            (is_upper(opcode))                                  -> imm_u,
            (opcode === OPCODE_STORE)                           -> imm_s,
            (opcode === OPCODE_BRANCH)                          -> imm_b,
            (opcode === OPCODE_JAL)                             -> imm_j
            ))

  dec_exe.pc := fch_dec.pc
  dec_exe.rs1 :=regbank.io.rs1_data
  dec_exe.rs2 :=regbank.io.rs2_data
  dec_exe.rd_addr  := fch_dec.instr(11, 7)
  dec_exe.wrb_ctrl.rd_wen := (opcode === OPCODE_REG_IMM ||
                              opcode === OPCODE_REG_REG ||
                              opcode === OPCODE_LOAD    ||
                              is_upper(opcode)          ||
                              is_jump(opcode))

  dec_exe.mem_ctrl.read   := opcode === OPCODE_LOAD
  dec_exe.mem_ctrl.write  := opcode === OPCODE_STORE
  dec_exe.wrb_ctrl.rd_sel := Mux(is_jump(opcode), RD_PC, RD_ALU)
}
