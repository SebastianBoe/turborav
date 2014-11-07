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

  val alu_in_a = dec_exe.rs1
  val alu_in_b = Mux(ctrl.alu_in_b_sel === ALU_IN_B_IMM,
                  dec_exe.imm,
                  dec_exe.rs2)

  val alu = Module(new Alu())
  alu.io.in_a := alu_in_a
  alu.io.in_b := alu_in_b
  alu.io.func := ctrl.alu_func
  io.exe_mem.alu_result := alu.io.out

  io.exe_mem <> dec_exe
}