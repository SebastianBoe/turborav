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

  val imm      = Reg(init = UInt(0))
  val alu_func = Reg(init = UInt(0))
  val rd_addr  = Reg(init = UInt(0))
  val rs1_data = Reg(init = UInt(0))
  val rs2_data = Reg(init = UInt(0))
  val alu_in_a = Reg(init = UInt(0))
  val alu_in_b = Reg(init = UInt(0))

  when(io.fch_dec.instr_valid && !io.stall){
    // default values
    alu_func := UInt(ALU_ADD_VAL)

    when(opcode === OPCODE_REG_REG) {
      alu_func := alu_func_r
      alu_in_a := ALU_IN_A_RS1
      alu_in_b := ALU_IN_B_RS2
    }
    .elsewhen(opcode === OPCODE_REG_IMM){
      when(isShift(alu_func_r)){
        imm := shamt
        alu_func := alu_func_r
      }
      .otherwise{
        imm := imm_i
        alu_func := alu_func_i
      }
      alu_in_a := ALU_IN_A_RS1
      alu_in_b := ALU_IN_B_IMM
    }
    .elsewhen(opcode === OPCODE_STORE){
      imm := imm_s
    }

    rs1_data := regbank.io.rs1_data
    rs2_data := regbank.io.rs2_data
    rd_addr  := io.fch_dec.instr(11, 7)
  }

  io.dec_exe.imm     := imm
  io.dec_exe.rd_addr := rd_addr
  io.dec_exe.rs1     := rs1_data
  io.dec_exe.rs2     := rs2_data

  io.dec_exe.exe_ctrl.alu_func := alu_func
  io.dec_exe.exe_ctrl.alu_in_a_sel := alu_in_a
  io.dec_exe.exe_ctrl.alu_in_b_sel := alu_in_b
}