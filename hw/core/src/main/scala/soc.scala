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
// Only the three lowest segments have defined meaning. The lowest
// segment is ROM, followed by RAM, and then memory mapped IO (AKA
// peripherals).

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

// Get the Memory map offsets from Constants.BASE_ADDR_XXX

class Soc extends Module {
  val io = new Bundle { val spi = new SpiIo() }

  val ravv    = Module(new RavV())
  val ram     = Module(new Ram ())
  val adapter = Module(new RRApbAdapter())
  val spi     = Module(new Spi())

  io.spi <> spi.io.spi

  // Connect the bus master
  ravv.io <> adapter.io.rr

  val master_apb = adapter.io.apb // For convenience
  val memory_segment = master_apb.addr(31, 28)
  val is_ram_request = MEMORY_SEGMENT_RAM === memory_segment
  val is_spi_request = MEMORY_SEGMENT_SPI === memory_segment

  // Bah, was tricky to make this beautiful, try again later.
  ram.io.addr   := clearIfDisabled(master_apb.addr  , enabled = is_ram_request)
  ram.io.wdata  := clearIfDisabled(master_apb.wdata , enabled = is_ram_request)
  ram.io.write  := clearIfDisabled(master_apb.write , enabled = is_ram_request)
  ram.io.sel    := clearIfDisabled(master_apb.sel   , enabled = is_ram_request)

  spi.io.apb.addr   := clearIfDisabled(master_apb.addr  , enabled = is_spi_request)
  spi.io.apb.sel    := clearIfDisabled(master_apb.sel   , enabled = is_spi_request)
  spi.io.apb.wdata  := clearIfDisabled(master_apb.wdata , enabled = is_spi_request)
  spi.io.apb.write  := clearIfDisabled(master_apb.write , enabled = is_spi_request)

  // This redundancy is bad, but when we get a tristate bus it can be deprecated.
  master_apb.enable := MuxCase(ram.io.enable, Array(
    is_ram_request -> ram.io.enable,
    is_spi_request -> spi.io.apb.enable
  ))
  master_apb.rdata := MuxCase(ram.io.rdata, Array(
    is_ram_request -> ram.io.rdata,
    is_spi_request -> spi.io.apb.rdata
  ))
  master_apb.ready := MuxCase(ram.io.ready, Array(
    is_ram_request -> ram.io.ready,
    is_spi_request -> spi.io.apb.ready
  ))

}

