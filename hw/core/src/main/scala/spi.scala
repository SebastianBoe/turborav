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

  // Use the implicit clock as SPI clock.
  val clk = Reg(Bool())
  when (Bool(true)) {
    clk := !io.clk
  }
  io.clk := clk


  val tx_reg = UInt(width=8)
  when (io.apb.sel && io.apb.write) {
    when (SPI_TX_BYTE_REG_ADDR === io.apb.addr) {
      tx_reg := io.apb.wdata
      } .otherwise {
        tx_reg := UInt(0, 8)
      }
  }
}
