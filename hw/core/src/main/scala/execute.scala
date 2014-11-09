package TurboRav

import Chisel._
import Common._
import Constants._

class Execute() extends Module {

  val io = new ExecuteIO()

  val dec_exe = Reg(init = new DecodeExecute())
  when(!io.stall){
    dec_exe := io.dec_exe
  }

  val ctrl = dec_exe.exe_ctrl
  val zero = UInt(0, width = Config.xlen)

  val alu_in_a = Mux(ctrl.alu_in_a_sel === ALU_IN_A_PC,  dec_exe.pc,
                 Mux(ctrl.alu_in_a_sel === ALU_IN_A_RS1, dec_exe.rs1,
                                                         zero))

  val alu_in_b = Mux(ctrl.alu_in_b_sel === ALU_IN_B_IMM,
                    dec_exe.imm,
                    dec_exe.rs2)

  val alu = Module(new Alu())
  alu.io.in_a := alu_in_a
  alu.io.in_b := alu_in_b
  alu.io.func := ctrl.alu_func
  io.exe_mem.alu_result := alu.io.out

  val bru = Module(new BranchUnit())
  bru.io.in_a := dec_exe.rs1
  bru.io.in_b := dec_exe.rs2
  bru.io.func := ctrl.bru_func
  val branch_take = bru.io.take

  io.exe_fch.pc_sel:= Mux(bru.io.take || ctrl.jump,
                          PC_SEL_BRJMP,
                          PC_SEL_PC_PLUS4)

  io.exe_mem <> dec_exe
}