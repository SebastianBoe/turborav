package TurboRav

import Chisel._
import Common._
import Constants._

// The System-on-Chip module.

// The SoC module instantiates and connects with an apb bus the
// processor to the APB peripherals. It is the toplevel of Chisel
// code, if we go any higher we venture into nasty fpga-specific
// verilog.

// See MemoryMapUtil for how the memory map is laid out.

class Soc extends Module {
  val io = new Bundle { val spi = new SpiIo() }

  val ravv    = Module(new RavV())
  val adapter = Module(new RRApbAdapter())
  val spi     = Module(new Spi())

  io.spi <> spi.io.spi

  // Connect the bus master
  ravv.io <> adapter.io.rr

  val master_apb = adapter.io.apb // For convenience
  val is_spi_request = isInSpiSegment(master_apb.addr)

  // Bah, was tricky to make this beautiful, try again later.
  spi.io.apb.addr   := clearIfDisabled(master_apb.addr  , enabled = is_spi_request)
  spi.io.apb.sel    := clearIfDisabled(master_apb.sel   , enabled = is_spi_request)
  spi.io.apb.wdata  := clearIfDisabled(master_apb.wdata , enabled = is_spi_request)
  spi.io.apb.write  := clearIfDisabled(master_apb.write , enabled = is_spi_request)

  // This redundancy is bad, but when we get a tristate bus it can be deprecated.
  master_apb.enable := MuxCase(Bool(false), Array(
    is_spi_request -> spi.io.apb.enable
  ))
  master_apb.rdata := MuxCase(UInt(0), Array(
    is_spi_request -> spi.io.apb.rdata
  ))
  master_apb.ready := MuxCase(Bool(false), Array(
    is_spi_request -> spi.io.apb.ready
  ))
}
