package TurboRav

import Chisel._
import Common._
import Constants._

class Decode() extends Module {

  require(Config.xlen == 32 || Config.xlen == 64 || Config.xlen == 128)

  val io = new DecodeIO()

  // all shift codes end in 01
  def isShift(func: Bits) = (!func(1)) && func(0)

  val rs1_addr = io.fch_dec.instr(19, 15)
  val rs2_addr = io.fch_dec.instr(24, 20)

  val opcode     = io.fch_dec.instr(6, 0)
  val alu_func_r = Cat(io.fch_dec.instr(30),io.fch_dec.instr(14, 12))
  val alu_func_i = Cat(UInt(0, width = 1), io.fch_dec.instr(14, 12))

  //Sign extended immediates
  val imm_i = Cat(Fill(io.fch_dec.instr(31), Config.xlen - 12),
                  io.fch_dec.instr(31, 20))

  val imm_s = Cat(Fill(io.fch_dec.instr(31), Config.xlen - 12),
                  io.fch_dec.instr(31, 25),
                  io.fch_dec.instr(11, 7))

  val imm_b = Cat(Fill(io.fch_dec.instr(31), Config.xlen - 12 - 1),
                  io.fch_dec.instr(7),
                  io.fch_dec.instr(30, 25),
                  io.fch_dec.instr(11, 8),
                  UInt(0, width = 1))

  val imm_j = Cat(Fill(io.fch_dec.instr(31), Config.xlen - 20),
                  io.fch_dec.instr(19, 12),
                  io.fch_dec.instr(20),
                  io.fch_dec.instr(30, 21),
                  UInt(0, width = 1))

  val imm_u32 = Cat(io.fch_dec.instr(31, 20),
                    UInt(0, width = Config.xlen - 12))
  val imm_u = if(Config.xlen != 32)
              Cat(Fill(imm_u32(31), Config.xlen - 32), imm_u32)
              else imm_u32

  val shamt = Cat(UInt(0, width = Config.xlen - 5),
                  io.fch_dec.instr(24, 20))

  val regbank = Module(new RegBank())
  regbank.io.rs1_addr := rs1_addr
  regbank.io.rs2_addr := rs2_addr
  io.wrb_dec <> regbank.io

  val dec_exe = Reg(init = new DecodeExecute())

  when(io.fch_dec.instr_valid && !io.stall){
    // default values
    dec_exe.exe_ctrl.alu_func := UInt(ALU_ADD_VAL)
    dec_exe.exe_ctrl.alu_in_a_sel := ALU_IN_A_RS1

    when(opcode === OPCODE_REG_REG) {
      dec_exe.exe_ctrl.alu_func := alu_func_r
      dec_exe.exe_ctrl.alu_in_b_sel := ALU_IN_B_RS2
    }
    .elsewhen(opcode === OPCODE_REG_IMM){
      when(isShift(alu_func_r)){
        dec_exe.imm := shamt
        dec_exe.exe_ctrl.alu_func := alu_func_r
      }
      .otherwise{
        dec_exe.imm := imm_i
        dec_exe.exe_ctrl.alu_func := alu_func_i
      }
      dec_exe.exe_ctrl.alu_in_b_sel := ALU_IN_B_IMM
    }
    .elsewhen(opcode === OPCODE_STORE){
      dec_exe.imm := imm_s
    }
    dec_exe.rs1 := regbank.io.rs1_data
    dec_exe.rs2 := regbank.io.rs2_data
    dec_exe.rd_addr  := io.fch_dec.instr(11, 7)
  }

  io.dec_exe := dec_exe
}