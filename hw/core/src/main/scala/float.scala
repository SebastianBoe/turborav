package TurboRav

import Chisel._
import Constants._
import Common._

class Float() extends Module {
  val xlen = Config.xlen // Extract xlen for convenience
  require(isPow2(xlen))

  val io = new Bundle(){
    // D: val in_fmt = UInt(INPUT, xlen)
    val in_func       = UInt(INPUT, MULT_FUNC_WIDTH)
    val in_rm         = UInt(INPUT, xlen) // rounding mode

    val in_a_sign     = UInt(INPUT, xlen)
    val in_a_exp      = UInt(INPUT, xlen)
    val in_a_fraction = UInt(INPUT, xlen)

    val in_b_sign     = UInt(INPUT, xlen)
    val in_b_exp      = UInt(INPUT, xlen)
    val in_b_fraction = UInt(INPUT, xlen)


    val out_res       = UInt(INPUT, xlen)
    val out_done      = Bool(OUTPUT)
  }

  
}
