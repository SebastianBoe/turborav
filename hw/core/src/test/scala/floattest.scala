package TurboRav

import Chisel._
import Constants._

class FloatTest(c: Float) extends Tester(c) {

  def poke_vals(a: BigInt, b: BigInt) {
    poke(c.io.in_a_sign,  a(31))
    poke(c.io.in_a_exp,   a(30,23))
    poke(c.io.in_a_fract, a(22,0))

    poke(c.io.in_b_sign,  b(31))
    poke(c.io.in_b_exp,   b(30,23))
    poke(c.io.in_b_fract, b(22,0))
  }

  def poke_add() {
    poke(c.io.in_func, 0)
    poke(c.io.in_rm, 0)  // Round to nearest
  }

  val f_2         = 0x40000000
  val f_1_5       = 0x3fc00000
  val f_2_5       = 0x40200000
  val f_n_2       = 0xc0000000
  val f_n_3_54321 = 0xc062c3f4
  val f_n_0_99999 = 0xbf7fff58

// Results
  val f_3_5       = 0x40600000

  poke_vals(f_2, f_1_5)
  poke_add()
  step(1000)
  expect(c.io.out_res, f_3_5)
}
