package TurboRav

import Chisel._

class DecodeTest(c: Decode) extends Tester(c) with Constants {

  poke(c.io.instr_valid, 1)

}