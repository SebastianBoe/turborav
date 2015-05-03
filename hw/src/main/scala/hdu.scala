package TurboRav

import Constants._
import Common._
import Chisel._

// Purely combinatorial Hazard Detection Unit
class HazardDetectionUnit() extends Module {

  val io = new HazardDetectionUnitIO()

  val load_use_rs1 = (
            ( io.hdu_mem.mem_read           &&
              io.hdu_mem.rd_addr != UInt(0) &&
              io.hdu_mem.rd_addr === io.hdu_exe.rs1_addr))

  val load_use_rs2 = (
            ( io.hdu_mem.mem_read           &&
              io.hdu_mem.rd_addr != UInt(0) &&
              io.hdu_mem.rd_addr === io.hdu_exe.rs2_addr))

  val load_use = load_use_rs1 || load_use_rs2

  val mult_busy = io.hdu_exe.mult_busy
  val mem_busy  = io.hdu_mem.mem_busy

  val fch_instr_valid = io.hdu_fch.instr_valid

  // It is the responsebility of the pipeline stage
  // to insert bubbles when it is stalling.
  val stall_mem = mem_busy
  val stall_exe = stall_mem ||  mult_busy || load_use
  val stall_dec = stall_exe
  val stall_fch = stall_dec || !fch_instr_valid

  io.hdu_fch.stall := stall_fch
  io.hdu_dec.stall := stall_dec
  io.hdu_exe.stall := stall_exe
  io.hdu_mem.stall := stall_mem
  io.hdu_wrb.stall := Bool(false)

}
