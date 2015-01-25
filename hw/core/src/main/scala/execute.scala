package TurboRav

import Chisel._
import Common._
import Constants._

class Execute() extends Module {

  val io = new ExecuteIO()

  val dec_exe = Reg(init = new DecodeExecute())
  when(!io.i_stall){
    dec_exe := io.dec_exe
  }

  val ctrl = dec_exe.exe_ctrl
  val zero = UInt(0, width = Config.xlen)

  val rs1 = Mux(io.fwu_exe.rs1_sel === RS_SEL_MEM, io.mem_exe.alu_result,
            Mux(io.fwu_exe.rs1_sel === RS_SEL_WRB, io.wrb_exe.rd_data,
                                                   dec_exe.rs1))

  val rs2 = Mux(io.fwu_exe.rs2_sel === RS_SEL_MEM, io.mem_exe.alu_result,
            Mux(io.fwu_exe.rs2_sel === RS_SEL_WRB, io.wrb_exe.rd_data,
                                                   dec_exe.rs2))

  val alu_in_a = Mux(ctrl.alu_in_a_sel === ALU_IN_A_PC,  dec_exe.pc,
                 Mux(ctrl.alu_in_a_sel === ALU_IN_A_RS1, rs1,
                                                         zero))

  val alu_in_b = Mux(ctrl.alu_in_b_sel === ALU_IN_B_IMM,
                    dec_exe.imm,
                    rs2)

  val alu = Module(new Alu())
  alu.io.in_a := alu_in_a
  alu.io.in_b := alu_in_b
  alu.io.func := ctrl.alu_func
  io.exe_mem.alu_result := alu.io.out

  val bru = Module(new BranchUnit())
  bru.io.in_a := dec_exe.rs1
  bru.io.in_b := dec_exe.rs2
  bru.io.func := ctrl.bru_func
  io.exe_fch.pc_alu := alu.io.out
  io.exe_fch.pc_sel := Mux(bru.io.take,
                           PC_SEL_BRJMP,
                           PC_SEL_PC_PLUS4)

  io.fwu_exe.rs1_addr := dec_exe.rs1_addr
  io.fwu_exe.rs2_addr := dec_exe.rs2_addr


  io.exe_mem <> dec_exe
}
