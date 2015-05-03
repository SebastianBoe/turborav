package TurboRav

// This package contains the constructs needed to connect to an APB
// bus. See ARM's documentation of APB for the signal meanings and
// protocol spec.

import Chisel._
import Common._

class SlaveToApbIo() extends Bundle {
  val addr  =  UInt(INPUT, Config.apb_addr_len)
  val wdata  = UInt(INPUT, Config.apb_data_len)
  val write =  Bool(INPUT)
  val sel   =  Bool(INPUT)

  val enable = Bool(OUTPUT)
  val rdata  = UInt(OUTPUT, Config.apb_data_len)
  val ready  = Bool(OUTPUT)
}
