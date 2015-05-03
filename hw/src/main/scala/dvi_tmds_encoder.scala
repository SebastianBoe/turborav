package TurboRav

import Chisel._
import Constants._
import Common._

class dvi_tmds_encoder_majority() extends Module {
    val io = new Bundle(){
        val in = UInt(INPUT, 8)
        val majority = UInt(OUTPUT, 1)
    }

}
 
class dvi_tmds_encoder() extends Module {
  val io = new Bundle(){
    val data = UInt(INPUT, 8)
    val ctrl0 = UInt(INPUT, 1)
    val ctrl1 = UInt(INPUT, 1)
    val data_enable = UInt(INPUT, 1)

    val result = UInt(OUTPUT, 10)
  }

  val sum_elements := in.foldLeft(0) { (sum, n) => sum + n }
  val majority := (sum_elements > 4) || (sum_elements == 4 && in(0))
}
