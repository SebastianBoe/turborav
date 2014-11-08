package TurboRav

import Chisel._
import Common._
import Constants._

class Decode() extends Module {

  require(Config.xlen == 32 || Config.xlen == 64 || Config.xlen == 128)

  // all shift codes end in 01 (func3 is 14:12)
  def isShift(func: Bits) = (!func(13)) && func(12)

  val io = new DecodeIO()

  val fch_dec = Reg(init = new FetchDecode())
  unless(io.stall){
    fch_dec := io.fch_dec
  }

  val rs1_addr = fch_dec.instr(19, 15)
  val rs2_addr = fch_dec.instr(24, 20)

  val opcode     = fch_dec.instr(6, 0)
  val alu_func_r = Cat(fch_dec.instr(30),fch_dec.instr(14, 12))
  val alu_func_i = Cat(UInt(0, width = 1), fch_dec.instr(14, 12))

  //Sign extended immediates
  val imm_i = Cat(Fill(fch_dec.instr(31), Config.xlen - 12),
                  fch_dec.instr(31, 20))

  val imm_s = Cat(Fill(fch_dec.instr(31), Config.xlen - 12),
                  fch_dec.instr(31, 25),
                  fch_dec.instr(11, 7))

  val imm_b = Cat(Fill(fch_dec.instr(31), Config.xlen - 12 - 1),
                  fch_dec.instr(7),
                  fch_dec.instr(30, 25),
                  fch_dec.instr(11, 8),
                  UInt(0, width = 1))

  val imm_j = Cat(Fill(fch_dec.instr(31), Config.xlen - 20),
                  fch_dec.instr(19, 12),
                  fch_dec.instr(20),
                  fch_dec.instr(30, 21),
                  UInt(0, width = 1))

  val imm_u32 = Cat(fch_dec.instr(31, 20),
                    UInt(0, width = Config.xlen - 12))
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

  exe_ctrl.alu_in_a_sel := ALU_IN_A_RS1
  exe_ctrl.alu_in_b_sel := Mux(opcode === OPCODE_REG_IMM,
                               ALU_IN_B_IMM,
                               ALU_IN_B_RS2)

  exe_ctrl.alu_func := Mux(opcode === OPCODE_REG_IMM && !isShift(fch_dec.instr),
                          alu_func_i,
                          alu_func_r)

  dec_exe.imm := MuxCase( UInt(0), Array(
            (opcode === OPCODE_REG_IMM && isShift(fch_dec.instr)) -> shamt,
            (opcode === OPCODE_REG_IMM ) -> imm_i,
            (opcode === OPCODE_STORE) -> imm_s
            ))

  dec_exe.rs1 :=regbank.io.rs1_data
  dec_exe.rs2 :=regbank.io.rs2_data
  dec_exe.rd_addr  := fch_dec.instr(11, 7)
  dec_exe.wrb_ctrl.rd_wen := (opcode === OPCODE_REG_IMM ||
                              opcode === OPCODE_REG_REG)
}