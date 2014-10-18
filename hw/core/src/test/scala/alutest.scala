/* Fancy header */

package TurboRav

import Chisel._

class AluTest(c: Alu) extends Tester(c) with Constants {

    /* Testing with bigint because of possible use of 64bit
     * values would couse problems with long types
     */
    def test(a: BigInt, b: BigInt, op: Int, result: BigInt){
        poke(c.io.inA, a)
        poke(c.io.inB, b)
        poke(c.io.func, op)
        expect(c.io.out, result)
    }
    val one = BigInt(1)
    val max = (one << c.xlen) - one

    // ADD tests
    test(0, 0, ADD, 0)
    test(one, 0, ADD, one)
    test(max, one, ADD, 0)
    test(max, max, ADD, max - one)
    for(i <- 0 until 10){
        val a = BigInt(c.xlen, rnd)
        val b =  BigInt(c.xlen, rnd)
        // mask out overflow bits
        val res = (a + b) & max
        test(a, b, ADD, res)
    }
}