package TurboRav

import Chisel._
import Constants._

class dvi_tmds_encoderTest(c: dvi_tmds_encoder) extends Tester(c) {

  step(1)
  poke(c.io.d, 3)
  poke(c.io.de, 0)
  for (i <- 0 to 3) {
    poke(c.io.c, i)
    expect(
      c.io.q_out,
      List(
        ScalaUtil.b("0010101011"),
        ScalaUtil.b("1101010100"),
        ScalaUtil.b("0010101010"),
        ScalaUtil.b("1101010101")
      )(i)
    )
  }
}
