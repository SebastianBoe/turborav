package TurboRav

import Chisel._
import Constants._

class dvi_tmds_encoderTest(c: dvi_tmds_encoder) extends Tester(c) {
  step(1)
  poke(c.io.d, UInt(3))
  poke(c.io.c, UInt("0b11"))
  poke(c.io.de, Bool(false))
  for (i <- 0 to 3) {
    poke(c.io.c, 
    expect(c.
  }
  expect(c.io.q_out, UInt("0b1101010101"))
}
