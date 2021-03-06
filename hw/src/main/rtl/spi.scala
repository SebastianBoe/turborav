// Copyright (C) 2015 Sebastian Bøe, Joakim Andersson
// License: BSD 2-Clause (see LICENSE for details)

package TurboRav

import Chisel._
import Constants._

class SpiIo() extends Bundle {
  val miso    = Bool(INPUT)
  val mosi    = Bool(OUTPUT)
  val clk = Bool(OUTPUT)
  val ncs     = Bool(OUTPUT)
}

class Spi extends Module {
  val io = new Bundle {
    val apb_slave = new ApbSlaveIo()
    val spi = new SpiIo()
  }

  val s_idle :: s_tx :: s_rx :: Nil = Enum(UInt(), 3)
  val state = Reg(init = s_idle)

  // Use the implicit clock as SPI clock.
  val spi_clk = Reg(Bool())
  when (Bool(true)) {
    spi_clk := !io.spi.clk
  }
  io.spi.clk := spi_clk

  val peripheral_address = io.apb_slave.in.addr(27, 0)
  // TODO: Use log2up
  val bits_sent = Reg(init=UInt(1, width=4))

  val tx_reg = Reg(UInt(width=8))

  when (io.apb_slave.sel && io.apb_slave.in.write) {
    tx_reg := io.apb_slave.in.wdata
    state := s_tx
  } .elsewhen (bits_sent === UInt(8)) {
    state := s_idle
  }

  when (state === s_tx) {
    io.spi.ncs := Bool(false)
    io.spi.mosi := tx_reg(0)
    bits_sent := bits_sent + UInt(1)
  } .otherwise {
    io.spi.ncs := Bool(true)
    io.spi.mosi := UInt(0)
  }
}
