// Copyright (C) 2015 Sebastian Bøe, Joakim Andersson
// License: BSD 2-Clause (see LICENSE for details)

package TurboRav

import Chisel._

// Purely combinatorial Hazard Detection Unit

// The hazard detection unit collects information from the different
// stages that could lead to a bubble and then distributes stall
// signals accordingly.

class HazardDetectionUnit extends Module {
  val io = new HazardDetectionUnitIO()

  val load_use = All(
    io.hdu_mem.mem_read,
    io.hdu_mem.rd_addr =/= UInt(0),
    io.hdu_mem.rd_addr === io.hdu_exe.rs2_addr ||
    io.hdu_mem.rd_addr === io.hdu_exe.rs1_addr
  )

  val mult_busy    = io.hdu_exe.mult_busy
  val mem_busy     = io.hdu_mem.mem_busy
  val branch_taken = io.hdu_exe.branch_taken

  // It is the responsibility of the pipeline stage to insert bubbles
  // when it is stalling.
  val stall_wrb = Bool(false)
  val stall_mem = stall_wrb || mem_busy
  val stall_exe = stall_mem || mult_busy || load_use
  val stall_dec = stall_exe
  val stall_fch = stall_dec

  io.hdu_wrb.stall := stall_wrb
  io.hdu_mem.stall := stall_mem
  io.hdu_exe.stall := stall_exe
  io.hdu_dec.stall := stall_dec
  io.hdu_fch.stall := stall_fch

  // Don't flush mem or wrb because we don't speculatively issue
  // instructions there (At least I hope we don't). Also don't flush
  // fch, because it uses the branch_taken signal to know when to
  // flush.
  io.hdu_exe.flush := branch_taken
  io.hdu_dec.flush := branch_taken
}
