package TurboRav

import Chisel._
import Constants._

class MultTest(c: Mult) extends Tester(c) {

  def test(a: BigInt, b: BigInt, func: Int, resL: BigInt, resH: BigInt) {
    poke(c.io.enable, 1)
    poke(c.io.in_a, a)
    poke(c.io.in_b, b)
    poke(c.io.func, func)
    // step once and deassert enable
    step(1)
    poke(c.io.enable, 0)
    // mult and div takes xlen + 1 cycles to complete
    step(c.xlen)
    expect(c.io.done, 1)
    expect(c.io.out_lo, resL)
    expect(c.io.out_hi, resH)
  }

  val one = BigInt(1)
  val two = BigInt(2)
  val max = (one << c.xlen) - one


  // TEST multiplication
  test(0, 0, MULT_MUL_VAL, 0, 0)
  test(one, 0, MULT_MUL_VAL, 0, 0)
  test(0, one, MULT_MUL_VAL, 0, 0)
  test(one, one, MULT_MUL_VAL, one, 0)
  test(two, two, MULT_MUL_VAL, BigInt(4), 0)

  test(max, one, MULT_MUL_VAL, max, 0)
  test(one, max, MULT_MUL_VAL, max, 0)
  test(max, two, MULT_MUL_VAL, max - one, 1)
  test(two, max, MULT_MUL_VAL, max - one, 1)

  /* Tests the carry out during multiplication */
 test(max, max, MULT_MUL_VAL, one, max - one)
 test(max/2, max, MULT_MUL_VAL, ((max/2)*max)&max, (max/2)*max >>c.xlen)
 test(max, max/2, MULT_MUL_VAL, ((max/2)*max)&max, (max/2)*max >>c.xlen)
 test(max, max, MULT_MUL_VAL, one, max - one)

 for(i <- 0 until 10){
   val a = BigInt(c.xlen, rnd)
   val b = BigInt(c.xlen, rnd)
   var res_lo =  (a * b) & max
   val res_hi = (a * b) >> c.xlen
   test(a, b, MULT_MUL_VAL, res_lo, res_hi)
 }


 // TEST division
 test(0, one, MULT_DIVU_VAL, 0, 0)
 test(one, one, MULT_DIVU_VAL, one, 0)
 test(two, one, MULT_DIVU_VAL, two, 0)
 test(max, one, MULT_DIVU_VAL, max, 0)
 test(max, max, MULT_DIVU_VAL, one, 0)
 test(max, two, MULT_DIVU_VAL, max/two, 1)

 for(i <- 0 until 10){
   val a = BigInt(c.xlen, rnd)
   val b = BigInt(c.xlen, rnd)
   var res =  a / b
   val rem =  a - (a/b)*b
   test(a, b, MULT_DIVU_VAL, res, rem)
 }

 // TEST abort
 poke(c.io.enable, 1)
 poke(c.io.in_a, two)
 poke(c.io.in_b, one)
 poke(c.io.func, MULT_DIVU_VAL)
 // step once and deassert enable
 step(1)
 poke(c.io.enable, 0)
 step(c.xlen/2)
 // abort and step once
 poke(c.io.abort, 1)
 step(1)
 poke(c.io.abort, 0)

 test(two, one, MULT_MUL_VAL, two, 0)

}
