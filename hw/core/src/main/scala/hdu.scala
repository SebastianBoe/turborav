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

  val fetchValidInstruction = io.hdu_fch.instructionValid

  // It is the responsebility of the pipeline stage
  // to insert bubbles when it is stalling.
  io.hdu_fch.stall := load_use || mult_busy || !fetchValidInstruction
  io.hdu_dec.stall := load_use || mult_busy
  io.hdu_exe.stall := load_use || mult_busy
  io.hdu_mem.stall := Bool(false)
  io.hdu_wrb.stall := Bool(false)

}
