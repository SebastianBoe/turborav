package TurboRav

import Chisel._
import Constants._
import Common._

class Timer() extends Module {
  val xlen = Config.xlen 
  require(isPow2(xlen))

  val io = new Bundle() {
    val out_val   = UInt(OUTPUT, xlen)
  }

  val state = Reg(init = UInt(0,width=xlen))

  when(Bool(true))
  {
    state := state + UInt(1)
  }
  io.out_val := state 
}
