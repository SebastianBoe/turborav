package TurboRav

import Chisel._
import Common._
import Constants._

// The System-on-Chip module.

// The SoC module instantiates and connects with an apb bus the
// processor, ram, rom, and other peripherals. It is the toplevel of
// Chisel code, if we go any higher we venture into nasty
// fpga-specific verilog.

// Memory map
// The two most significant bits divide the memory map into 4 parts.

// Bit 31,30
//      1, 0 // Reserved for future use.
//      1, 1 // Reserved for future use.
//      0, 1 // RAM memory map
//      0, 0 // ROM memory map

class Soc extends Module {
  val io = new Bundle { val stub = Bool(INPUT) }

  val ravv    = Module(new RavV())
  val rom     = Module(new Rom ())
  val ram     = Module(new Ram ())
  val adapter = Module(new RRApbAdapter())

  ravv.io <> adapter.io.rr
  rom .io <> adapter.io.apb
}
