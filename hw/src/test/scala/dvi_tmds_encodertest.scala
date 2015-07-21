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

  step(1)

  poke(c.io.de, 1)
  poke(c.io.d, ScalaUtil.b("10101010"))
  poke(c.io.c, 1)

  // The first branch in the flow-chart on page 29 is TRUE.
  // q_m[0] = 0
  // q_m[1] = ! ( 0 xor 1) = 0
  // q_m[2] = ! (q_m[1] xor D[2]) = ! ( 0 xor 0) = ! 0 = 1
  // q_m[3] = ! (q_m[2] xor D[3]) = ! ( 1 xor 1) = ! 0 = 1
  // q_m[4] = ! (q_m[3] xor D[4]) = ! ( 1 xor 0) = ! 1 = 0
  // q_m[5] = ! (q_m[4] xor D[5]) = ! ( 0 xor 1) = ! 1 = 0
  // q_m[6] = ! (q_m[5] xor D[6]) = ! ( 0 xor 0) = ! 0 = 1
  // q_m[7] = ! (q_m[6] xor D[7]) = ! ( 1 xor 1) = ! 0 = 1
  // q_m[8] =                                            0

  // Next step in flow-chart

  // q_out[9] = ! q_m[8] = 1
  // q_out[8] =   q_m[8] = 0
  // q_out[7] = ! q_m[7] = 0
  // q_out[6:0] = 0110011

  expect(c.io.q_out, ScalaUtil.b("1000110011"))

  // The encoder works for 5 of 2048 possible inputs, so I assume it
  // works for the other 2043 inputs.
}
