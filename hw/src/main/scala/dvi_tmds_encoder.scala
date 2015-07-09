package TurboRav

import Chisel._
import Constants._
import Common._

// This class tries to implement the flow chart on page 29 in the DVI spec V1.0
class dvi_tmds_encoder() extends Module {
  val io = new Bundle(){
    val data = UInt(INPUT, 8)
    val ctrl0 = UInt(INPUT, 1)
    val ctrl1 = UInt(INPUT, 1)
    val data_enable = UInt(INPUT, 1)

    val xored = UInt(OUTPUT, 9)
    //val result = UInt(OUTPUT, 10)
  }

  val prev_cnt = Reg(init = UInt(0, 3))
  val cnt      = Reg(init = UInt(0, 3))

  cnt := cnt + PopCount

  val majority = ((PopCount(io.data) > UInt(4))) || (PopCount(io.data) === UInt(4) && (io.data(0) === UInt(1)))

  val data_xored  = Vec.fill(9){ UInt(width = 1) }
  data_xored(0) := io.data(0)
  data_xored(8) := !majority

  for (i <- 1 to 7)
    data_xored(i) := Mux(majority, !(data_xored(i-1) ^ io.data(i)), data_xored(i-1) ^ io.data(i))

  io.xored := Cat(data_xored(8),data_xored(7), data_xored(6), data_xored(5), data_xored(4), data_xored(3), data_xored(2), data_xored(1), data_xored(0))
}
