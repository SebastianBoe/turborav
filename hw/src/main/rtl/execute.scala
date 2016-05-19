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

  val flushed_pipeline = new DecodeExecute()
  flushed_pipeline.exe_ctrl.bru_func := BNOT

  val stall = Wire(Bool())

  // Determine what should be written to the pipeline registers
  val dec_exe_next = MuxCase(
    io.dec_exe,
    Array(
      stall            -> dec_exe,
      io.hdu_exe.flush -> flushed_pipeline
    )
  )
  // Write to the pipeline registers
  dec_exe := dec_exe_next

  // This input doesn't have to go into the pipeline registers because
  // it is fed straight into regbank. And it does not need to be
  // flushed because writes from the writeback stage cannot be
  // cancelled in the current micro-architecture.
  val wrb_exe_next = io.wrb_exe

  // Create a register file and combinatorially connect it to the
  // decode stage for reads and the writeback stage for writes.
  val regbank = Module(new RegBank())
  regbank.io.reads := dec_exe_next.reg_reads
  regbank.io.write := wrb_exe_next.reg_write

  val ctrl = dec_exe.exe_ctrl
  val zero = UInt(0, width = Config.xlen)

  val rs_sel_mapping = Array(
    RS_SEL_MEM -> io.mem_exe.alu_result_or_mult_result,
    RS_SEL_WRB -> io.wrb_exe.rd_data
  )

  val rs1 = Lookup(io.fwu_exe.rs1_sel, regbank.io.rs1_data, rs_sel_mapping)
  val rs2 = Lookup(io.fwu_exe.rs2_sel, regbank.io.rs2_data, rs_sel_mapping)

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

  val mult_enable = dec_exe.exe_ctrl.mult_enable
  val mult_func = dec_exe.exe_ctrl.mult_func
  val s_normal :: s_mult :: Nil = Enum(UInt(), 2)
  val state = Reg(init = s_normal)

  val mult = Module(new Mult())
  mult.io.in_a   := rs1
  mult.io.in_b   := rs2
  mult.io.func   := mult_func
  mult.io.enable := (mult_enable && state === s_normal)
  mult.io.abort  := Bool(false) //TODO: What happens if the mult instr is flushed?

  when(state === s_normal && mult_enable){
    state := s_mult
  }
  .elsewhen (!(state === s_mult && !mult.io.done)){
    state := s_normal
  }

  /* Stall as long as multiplication is executing */
  io.hdu_exe.mult_busy := Any(
    (state === s_normal) && mult_enable,
    (state === s_mult  ) && !mult.io.done
  )

  io.exe_mem.alu_result := alu.io.out
  io.exe_mem.mult.valid := state === s_mult
  io.exe_mem.mult.bits.result := Mux(
    isMultUpper(mult_func),
    mult.io.out_hi,
    mult.io.out_lo
  )

  io.exe_fch.pc_alu := alu.io.out
  io.exe_fch.branch_taken := bru.io.take

  io.hdu_exe.branch_taken := bru.io.take

  io.fwu_exe.rs1_addr := dec_exe.reg_reads.rs1.bits
  io.fwu_exe.rs2_addr := dec_exe.reg_reads.rs2.bits
  io.hdu_exe.rs1_addr := dec_exe.reg_reads.rs1.bits
  io.hdu_exe.rs2_addr := dec_exe.reg_reads.rs2.bits

  io.exe_mem.rs2 := rs2

  // We should stall either when the external Hazard Detection Unit
  // says so, or when we internally detect that we are doing a
  // multi-cycle multiply/divide operation.
  stall := io.hdu_exe.stall || io.hdu_exe.mult_busy

  when(io.hdu_exe.stall){
    io.kill()
  }
}
