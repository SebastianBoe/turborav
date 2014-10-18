package TurboRav

import Chisel._

class Alu (val xlen: Int) extends Module with Constants {
  val io = new Bundle {
    val func = UInt(INPUT, 4)
    val inA = UInt(INPUT, xlen)
    val inB = UInt(INPUT, xlen)
    val out = UInt(OUTPUT, xlen)
  }
  val result = Mux(io.func === ALU_ADD, io.inA + io.inB,
                                        UInt(0))
  io.out := result
}
