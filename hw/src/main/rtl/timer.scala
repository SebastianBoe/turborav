// Copyright (C) 2015 Sebastian Bøe, Joakim Andersson
// License: BSD 2-Clause (see LICENSE for details)

package TurboRav

import Chisel._
import Constants._

/*
  in_start: Start timer
  in_reset: Reset timer value to 0
            NOTE: To get the timer running again you need to disable this
                  signal.

  out_val:  Used to read out current timer value
*/

class Timer extends Module {
  val xlen = Config.xlen
  require(isPow2(xlen))

  val io = new Bundle {
    val in_start  = Bool(INPUT)
    val in_reset  = Bool(INPUT)

    val out_val   = UInt(OUTPUT, xlen)
  }

  val state   = Reg(init = UInt(0, width = xlen))

  when(io.in_start) {
    state := state + UInt(1)
  }

  when(io.in_reset) {
    state := UInt(0)
  }

  io.out_val := state
}
