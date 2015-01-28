package TurboRav

import Chisel._

import Apb._
import Common._
import Constants._

class Spi extends Module {
  val io = new Bundle {
    val apb = new SlaveToApbIo()

    val miso  = Bool(INPUT)

    val mosi  = Bool(OUTPUT)
    val spi_clk   = Bool(OUTPUT)
    val ncs   = Bool(OUTPUT)
  }

  val s_idle :: s_tx :: s_rx :: Nil = Enum(UInt(), 3)
  val state = Reg(init = s_idle)

  // Use the implicit clock as SPI clock.
  val spi_clk = Reg(Bool())
  when (Bool(true)) {
    spi_clk := !io.spi_clk
  }
  io.spi_clk := spi_clk

  val peripheral_address = io.apb.addr(27, 0)
  // TODO: Use log2up
  val bits_sent = Reg(init=UInt(1, width=4))

  val tx_reg = Reg(UInt(width=8))

  when (io.apb.sel && io.apb.write) {
    tx_reg := io.apb.wdata
    state := s_tx
  } .elsewhen (bits_sent === UInt(8)) {
    state := s_idle
  }

  when (state === s_tx) {
    io.ncs := Bool(false)
    io.mosi := tx_reg(0)
    bits_sent := bits_sent + UInt(1)
  } .otherwise {
    io.ncs := Bool(true)
    io.mosi := UInt(0)
  }

}
