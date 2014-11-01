package TurboRav

import Chisel._
import Constants._

class DecodeTest(c: Decode) extends Tester(c) {

  poke(c.io.instr_valid, 1)

}