/* Fancy header */

package TurboRav

import Chisel._

class Alu extends Module {
  val io = new Bundle { 
    val out = UInt(OUTPUT, 8)
  }
  io.out := UInt(42)
}
