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

// The 4 most significant bits divide the memory map into 16 segments.
// Only the three lowest segments are defined. The lowest segment is
// ROM, followed by RAM, and then memory mapped IO (AKA peripherals).

// Bit 31,30,29,28
//      0, 0, 0, 0 // ROM memory map
//      0, 0, 0, 1 // RAM memory map
//      0, 0, 1, 0 // MMIO // Coming in the future.
//      otherwise  // Reserved for future use

// See the following 32 bit addresses as an example
// 0x0000_0000 // Rom address
// 0x1000_0000 // Ram address
// 0x2000_0000 // Peripheral hardware register address
// 0x3000_0000 // Reserved

class Soc extends Module {
  val io = new Bundle { val stub = Bool(INPUT) }

  val ravv    = Module(new RavV())
  val rom     = Module(new Rom ())
  val ram     = Module(new Ram ())
  val adapter = Module(new RRApbAdapter())

  // Connect the bus master
  ravv.io <> adapter.io.rr

  val master_apb = adapter.io.apb // For convenience
  val is_ram_request = master_apb.addr(28)

  // Bah, was tricky to make this beautiful, try again later.
  ram.io.addr  := clearIfDisabled(master_apb.addr  , enabled = is_ram_request)
  ram.io.wdata := clearIfDisabled(master_apb.wdata , enabled = is_ram_request)
  ram.io.write := clearIfDisabled(master_apb.write , enabled = is_ram_request)
  ram.io.sel   := clearIfDisabled(master_apb.sel   , enabled = is_ram_request)

  rom.io.addr  := clearIfDisabled(master_apb.addr  , !is_ram_request)
  rom.io.sel   := clearIfDisabled(master_apb.sel   , !is_ram_request)

  master_apb.enable := Mux(is_ram_request , ram.io.enable , rom.io.enable)
  master_apb.rdata  := Mux(is_ram_request , ram.io.rdata  , rom.io.rdata)
  master_apb.ready  := Mux(is_ram_request , ram.io.ready  , rom.io.ready)
}
