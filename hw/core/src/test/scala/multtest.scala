package TurboRav

import Chisel._

class MultTest(c: Mult) extends Tester(c) with Constants {

    def test(a: BigInt, b: BigInt, func: Int, res: BigInt) {
        poke(c.io.enable, 1)
        poke(c.io.inA, a)
        poke(c.io.inB, b)
        poke(c.io.func, func)
        step(1)
        poke(c.io.enable, 0)
        peek(c.state)
        step(32) // ?? how many actually?
        expect(c.io.done, 1)
        expect(c.io.outL, res)
    }

    val one = BigInt(1)
    val two = BigInt(2)
    val max = (one << c.xlen) - one

    test(0, 0, MULT_MUL_VAL, 0)
    test(one, 0, MULT_MUL_VAL, 0)
    test(0, one, MULT_MUL_VAL, 0)
    test(one, one, MULT_MUL_VAL, one)
    test(two, two, MULT_MUL_VAL, BigInt(4))

    test(max, one, MULT_MUL_VAL, max)
    test(one, max, MULT_MUL_VAL, max)
    test(max, two, MULT_MUL_VAL, max - one)
    test(two, max, MULT_MUL_VAL, max - one)
    test(max, max, MULT_MUL_VAL, one)

    for(i <- 0 until 10){
        val a = BigInt(c.xlen, rnd)
        val b = BigInt(c.xlen, rnd)
        var res =  (a * b) & max
        test(a, b, MULT_MUL_VAL, res)
    }

}