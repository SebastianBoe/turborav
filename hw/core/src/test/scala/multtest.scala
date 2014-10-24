package TurboRav

import Chisel._

class MultTest(c: Mult) extends Tester(c) with Constants {

    def test(a: BigInt, b: BigInt, func: Int, resL: BigInt, resH: BigInt) {
        poke(c.io.enable, 1)
        poke(c.io.inA, a)
        poke(c.io.inB, b)
        poke(c.io.func, func)
        // step once and deassert enable
        step(1)
        poke(c.io.enable, 0)
        // mult and div takes xlen + 1 cycles to complete
        step(c.xlen)
        expect(c.io.done, 1)
        expect(c.io.outL, resL)
        expect(c.io.outH, resH)
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
        var resL =  (a * b) & max
        val resH = (a * b) >> c.xlen
        test(a, b, MULT_MUL_VAL, resL, resH)
    }


    // TEST division
    test(0, one, MULT_DIV_VAL, 0, 0)
    test(one, one, MULT_DIV_VAL, one, 0)
    test(two, one, MULT_DIV_VAL, two, 0)
    test(max, one, MULT_DIV_VAL, max, 0)
    test(max, max, MULT_DIV_VAL, one, 0)
    test(max, two, MULT_DIV_VAL, max/two, 1)

    for(i <- 0 until 10){
        val a = BigInt(c.xlen, rnd)
        val b = BigInt(c.xlen, rnd)
        var res =  a / b
        val rem =  a - (a/b)*b
        test(a, b, MULT_DIV_VAL, res, rem)
    }

    // TEST abort
    poke(c.io.enable, 1)
    poke(c.io.inA, two)
    poke(c.io.inB, one)
    poke(c.io.func, MULT_DIV_VAL)
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