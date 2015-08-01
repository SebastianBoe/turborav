package TurboRav

import Chisel._
import Constants._

class Execute extends Module {

  private def isMultUpper(mult_func: Bits) = {
    mult_func === MULT_MULH   ||
    mult_func === MULT_MULHU  ||
    mult_func === MULT_MULHSU ||
    mult_func === MULT_REM    ||
    mult_func === MULT_REMU
  }

  private def isMultLower(mult_func: Bits) = {
    mult_func === MULT_MUL  ||
    mult_func === MULT_DIV  ||
    mult_func === MULT_DIVU
  }

  val io = new ExecuteIO()

  val dec_exe = Reg(init = new DecodeExecute())

  // Default to pipelining decode's values.
  io.exe_mem := dec_exe

  // But if we stall then insert a bubble by killing exe_mem.
  when(io.hdu_exe.stall){
    io.exe_mem.kill()
  } .otherwise {
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

  val bru = Module(new BranchUnit())
  bru.io.in_a := rs1
  bru.io.in_b := rs2
  bru.io.func := ctrl.bru_func
  val pc_sel = Mux(bru.io.take,
                           PC_SEL_BRJMP,
                           PC_SEL_PC_PLUS4)


  val mult_enable = dec_exe.exe_ctrl.mult_enable
  val mult_func = dec_exe.exe_ctrl.mult_func
  val s_normal :: s_mult :: Nil = Enum(UInt(), 2)
  val state = Reg(init = s_normal)

  val mult = Module(new Mult())
  mult.io.in_a   := rs1
  mult.io.in_b   := rs2
  mult.io.func   := mult_func
  mult.io.enable := (mult_enable && state === s_normal)
  mult.io.abort  := Bool(false)

  when(state === s_normal && mult_enable){
    state := s_mult
  }
  .elsewhen (!(state === s_mult && !mult.io.done)){
    state := s_normal
  }

  /* Stall as long as multiplication is executing */
  io.hdu_exe.mult_busy :=
    ((state === s_normal) && mult_enable)  ||
    ((state === s_mult)   && !mult.io.done)

  io.exe_mem.alu_result :=
     Mux(isMultUpper(mult_func) && state === s_mult, mult.io.out_hi,
     Mux(isMultLower(mult_func) && state === s_mult, mult.io.out_lo,
                                                     alu.io.out))

  io.exe_fch.pc_alu := alu.io.out
  io.exe_fch.pc_sel := pc_sel
  io.dec_exe.pc_sel := pc_sel

  io.fwu_exe.rs1_addr := dec_exe.rs1_addr
  io.fwu_exe.rs2_addr := dec_exe.rs2_addr
  io.dec_exe.pc_sel   := bru.io.take

  io.hdu_exe.rs1_addr := dec_exe.rs1_addr
  io.hdu_exe.rs2_addr := dec_exe.rs2_addr

  io.exe_mem.rs2 := rs2
}
