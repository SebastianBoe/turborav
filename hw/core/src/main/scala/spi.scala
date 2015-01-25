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
    val clk   = Bool(OUTPUT)
    val ncs   = Bool(OUTPUT)
  }

  val s_idle :: s_tx :: s_rx :: Nil = Enum(UInt(), 3)
  val state = Reg(init = s_idle)

  // Use the implicit clock as SPI clock.
  val clk = Reg(Bool())
  when (Bool(true)) {
    clk := !io.clk
  }
  io.clk := clk

  val peripheral_address = io.apb.addr(27, 0)
  val bits_sent = Reg(init=UInt(0))

  val tx_reg = Reg(UInt(width=8))
  when (io.apb.sel && io.apb.write) {
    when (SPI_TX_BYTE_REG_ADDR === peripheral_address) {
      tx_reg := io.apb.wdata
      state := s_tx
      } .otherwise {
        tx_reg := UInt(0, 8)
      }
  } .elsewhen (bits_sent === UInt(8)) {
    state := s_idle
  }

  when (state === s_tx) {
    io.ncs := Bool(false)
    when (bits_sent <= UInt(8)) {
      //io.mosi := tx_reg(0)
      io.mosi := MuxCase(UInt(0), Array(
        (bits_sent === UInt(8)) -> tx_reg(7),
        (bits_sent === UInt(7)) -> tx_reg(6),
        (bits_sent === UInt(6)) -> tx_reg(5),
        (bits_sent === UInt(5)) -> tx_reg(4),
        (bits_sent === UInt(4)) -> tx_reg(3),
        (bits_sent === UInt(3)) -> tx_reg(2),
        (bits_sent === UInt(2)) -> tx_reg(1),
        (bits_sent === UInt(1)) -> tx_reg(0)
        ))
      bits_sent := bits_sent + UInt(1)
    }
  } .otherwise {
    io.ncs := Bool(true)
    io.mosi := UInt(0)
  }

  io.apb.rdata := tx_reg

}
