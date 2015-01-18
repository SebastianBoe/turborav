package TurboRav

import Chisel._
import Common._
import Constants._
import Apb._

// See RRApbAdapter for documentation about this bundle.
class RequestResponseIo extends Bundle {
  val request  = new ValidIO( new Bundle {
    val addr  = UInt(width = Config.xlen)
    val wdata = UInt(width = Config.xlen)
    val write = Bool()
  })

  val response = new ValidIO(new Bundle {
    val word = UInt(width = Config.xlen)
  }).flip()
}
