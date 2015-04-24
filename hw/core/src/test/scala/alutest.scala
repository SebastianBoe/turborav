package TurboRav

import Chisel._
import Common._
import Constants._

class AluTest(c: Alu) extends Tester(c) {
  val xlen = Config.xlen
  /* Testing with bigint because of possible use of 64bit
   * values would couse problems with long types
   */
  def test(a: BigInt, b: BigInt, op: Int, result: BigInt){
    poke(c.io.in_a, a)
    poke(c.io.in_b, b)
    poke(c.io.func, op)
    expect(c.io.out, result)
  }
  val one = BigInt(1)
  val max = (one << xlen) - one
  val min_signed = one << (xlen-1)
  val max_signed = (one << (xlen-1)) - one

  // ADD tests
  test(0,     0, ALU_ADD_VAL, 0)
  test(one,   0, ALU_ADD_VAL, one)
  test(max, one, ALU_ADD_VAL, 0)
  test(max, max, ALU_ADD_VAL, max - one)
  for(i     <- 0 until 10){
    val a = BigInt(xlen, rnd)
    val b = BigInt(xlen, rnd)
    // mask out overflow bits
    val res = (a + b) & max
    test(a, b, ALU_ADD_VAL, res)
  }

  // SUB tests
  test(one,   0, ALU_SUB_VAL, one)
  test(one, one, ALU_SUB_VAL, 0)
  test(0,   one, ALU_SUB_VAL, max)

  for(i <- 0 until 10){
    val a = BigInt(xlen, rnd)
    val b = BigInt(xlen, rnd)
    // mask out overflow bits
    val res = (a - b) & max
    test(a, b, ALU_SUB_VAL, res)
  }

  // SLT tests
  test(0,            0, ALU_SLT_VAL, 0)
  test(0,          one, ALU_SLT_VAL, one)
  test(one,          0, ALU_SLT_VAL, 0)
  // max is -1 in signed
  test(max,          0, ALU_SLT_VAL, one)
  test(max,        one, ALU_SLT_VAL, one)
  test(max_signed, one, ALU_SLT_VAL, 0)
  test(one, max_signed, ALU_SLT_VAL, one)
  test(min_signed, one, ALU_SLT_VAL, one)
  test(one ,min_signed, ALU_SLT_VAL, 0)

  // SLTU
  test(0,            0, ALU_SLTU_VAL, 0)
  test(0,          one, ALU_SLTU_VAL, one)
  test(one,          0, ALU_SLTU_VAL, 0)
  // max is -1 in signed
  test(max,          0, ALU_SLTU_VAL, 0)
  test(0,          max, ALU_SLTU_VAL, one)
  test(max_signed, one, ALU_SLTU_VAL, 0)
  test(one, max_signed, ALU_SLTU_VAL, one)
  // min signed is large unsigned
  test(min_signed, one, ALU_SLTU_VAL, 0)
  test(one, min_signed, ALU_SLTU_VAL, one)

  // AND test
  for(i <- 0 until 10){
    val a = BigInt(xlen, rnd)
    val b = BigInt(xlen, rnd)
    test(a, b, ALU_AND_VAL, a & b)
  }

  // OR test
  for(i <- 0 until 10){
    val a = BigInt(xlen, rnd)
    val b = BigInt(xlen, rnd)
    test(a, b, ALU_OR_VAL, a | b)
  }

  // XOR test
  for(i <- 0 until 10){
    val a = BigInt(xlen, rnd)
    val b = BigInt(xlen, rnd)
    test(a, b, ALU_XOR_VAL, a ^ b)
  }

  // SLL test
  test( 0, 0, ALU_SLL_VAL, 0)
  test( 0, xlen -1, ALU_SLL_VAL, 0)
  test( one, 0, ALU_SLL_VAL, one)
  test( one, xlen -1, ALU_SLL_VAL, min_signed)
  test( one, one, ALU_SLL_VAL, 2)
  test( max, one, ALU_SLL_VAL, max - one)
  test( max, xlen -1, ALU_SLL_VAL, min_signed)

  // SRL test
  test( 0, 0, ALU_SRL_VAL, 0)
  test( 0, xlen -1, ALU_SRL_VAL, 0)
  test( one, 0, ALU_SRL_VAL, one)
  test( one, xlen -1, ALU_SRL_VAL, 0)
  test( one, one, ALU_SRL_VAL, 0)
  test( max, one, ALU_SRL_VAL, max_signed)
  test( max, xlen -1, ALU_SRL_VAL, one)

  // SRA test
  test( 0, 0, ALU_SRA_VAL, 0)
  test( 0, xlen -1, ALU_SRA_VAL, 0)
  test( one, 0, ALU_SRA_VAL, one)
  test( one, xlen -1, ALU_SRA_VAL, 0)
  test( one, one, ALU_SRA_VAL, 0)
  test( max_signed, one, ALU_SRA_VAL, max_signed / 2)
  test( max_signed, xlen -1, ALU_SRA_VAL, 0)
  test( max, one, ALU_SRA_VAL, max)
  test( max, xlen -1, ALU_SRA_VAL, max)

}
