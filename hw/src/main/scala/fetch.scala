// Copyright (C) 2015 Sebastian Bøe, Joakim Andersson
// License: BSD 2-Clause (see LICENSE for details)

package TurboRav

import Chisel._
import Constants._

class Fetch extends Module {
  val io = new FetchIO()

  val exe_fch = Reg(init = new ExecuteFetch())

  val pc = Reg(init = UInt(0, width = Config.xlen))

  val pc_next = Mux(
    io.hdu_fch.stall,
    pc,
    Mux(
      exe_fch.branch_taken,
      exe_fch.pc_alu,
      pc + UInt(4)
    )
  )

  when(io.hdu_fch.stall){
    io.kill()
  } .otherwise {
    exe_fch := io.exe_fch
    pc      := pc_next
  }

  io.rr_io.kill()
  io.rr_io.request.bits.addr := pc_next
  io.rr_io.request.valid     := Bool(true)

  // Fetch to decode

  // pc_next is fed through because writeback needs it for
  // something. This costs us a few flip flops to feed it through so
  // far. TODO: See how the area cost compares if exe re-computes pc +
  // 4
  io.fch_dec.pc          := pc
  io.fch_dec.pc_next     := pc_next
  io.fch_dec.instr_valid := io.rr_io.response.valid

  // Send a NOP to Decode if we branch.
  // TODO: is 0 a NOP?
  // TODO: can we resolve this before the pipeline registers are
  // written to instead?
  io.fch_dec.instr := Mux(
    exe_fch.branch_taken,
    UInt(0),
    io.rr_io.response.bits.word
  )

  // TODO: What to do with the structural hazard of MEM and FCH both
  // wanting to access the same memory?
}
