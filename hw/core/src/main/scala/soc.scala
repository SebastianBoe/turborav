package TurboRav

import Chisel._
import Common._
import Constants._

// The System-on-Chip module.

// The SoC module instantiates and connects with an apb bus the
// processor, ram, rom, and other peripherals. It is the toplevel of
// Chisel code, if we go any higher we venture into nasty
// fpga-specific verilog.

class Soc extends Module {
  val io = new Bundle {}

  val ravv    = Module(new RavV())
  val rom     = Module(new Rom ())
  val adapter = Module(new RRApbAdapter())

  ravv.io <> adapter.io.rr
  rom .io <> adapter.io.apb
}
