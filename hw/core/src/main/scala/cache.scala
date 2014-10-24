package TurboRav

import Chisel._

class Cache extends Module {
  val io = new Bundle {
    val in  = Bool(INPUT)
    val out = Bool(OUTPUT)
  }

}

