package Apb

// This package contains the constructs needed to connect to an APB
// bus.

import Chisel._
import Common._

class SlaveToApbIo(implicit conf: TurboravConfig) extends Bundle {
  val addr =   UInt(INPUT, conf.apb_addr_len)
  val write =  Bool(INPUT)
  val sel =    Bool(INPUT)
  val enable = Bool(OUTPUT)
  val rdata =  UInt(OUTPUT, conf.apb_data_len)
  val ready =  Bool(OUTPUT)
}
