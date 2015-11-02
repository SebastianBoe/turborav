package TurboRav

import Constants._
import Chisel._

class BranchUnitTest(c: BranchUnit) extends JUnitTester(c) {

  def test(a: BigInt, b: BigInt, op: Int, result: Int){
    poke(c.io.in_a, a)
    poke(c.io.in_b, b)
    poke(c.io.func, op)
    expect(c.io.take, result)
  }

  val xlen = Config.xlen

  val one        = BigInt(1)
  val True       = 1
  val False      = 0
  val max        = (one << xlen) - one
  val min_signed = one << (xlen-1)
  val max_signed = (one << (xlen-1)) - one

  // BEQ
  test(0,                    0, BRANCH_BEQ_VAL, True)
  test(0,                  one, BRANCH_BEQ_VAL, False)
  test(one,                  0, BRANCH_BEQ_VAL, False)
  test(one,                one, BRANCH_BEQ_VAL, True)
  test(max,         max_signed, BRANCH_BEQ_VAL, False)
  test(max_signed,         max, BRANCH_BEQ_VAL, False)
  test(max,         min_signed, BRANCH_BEQ_VAL, False)
  test(min_signed,         max, BRANCH_BEQ_VAL, False)
  test(max_signed,  min_signed, BRANCH_BEQ_VAL, False)
  test(min_signed,  max_signed, BRANCH_BEQ_VAL, False)
  test(max,                max, BRANCH_BEQ_VAL, True)

  // BNE
  test(0,                    0, BRANCH_BNE_VAL, False)
  test(0,                  one, BRANCH_BNE_VAL, True)
  test(one,                  0, BRANCH_BNE_VAL, True)
  test(one,                one, BRANCH_BNE_VAL, False)
  test(max,         max_signed, BRANCH_BNE_VAL, True)
  test(max_signed,         max, BRANCH_BNE_VAL, True)
  test(max,         min_signed, BRANCH_BNE_VAL, True)
  test(min_signed,         max, BRANCH_BNE_VAL, True)
  test(max_signed,  min_signed, BRANCH_BNE_VAL, True)
  test(min_signed,  max_signed, BRANCH_BNE_VAL, True)
  test(max,                max, BRANCH_BNE_VAL, False)

  // BLT a < b (signed)
  test(0,                    0, BRANCH_BLT_VAL, False)
  test(0,                  one, BRANCH_BLT_VAL, True)
  test(one,                  0, BRANCH_BLT_VAL, False)
  test(one,                one, BRANCH_BLT_VAL, False)
  test(max,         max_signed, BRANCH_BLT_VAL, True) // Max is -1 signed
  test(max_signed,         max, BRANCH_BLT_VAL, False)
  test(max,         min_signed, BRANCH_BLT_VAL, False)
  test(min_signed,         max, BRANCH_BLT_VAL, True)
  test(max_signed,  min_signed, BRANCH_BLT_VAL, False)
  test(min_signed,  max_signed, BRANCH_BLT_VAL, True)
  test(max,                max, BRANCH_BLT_VAL, False)

  // BGE a >= b (signed)
  test(0,                    0, BRANCH_BGE_VAL, True)
  test(0,                  one, BRANCH_BGE_VAL, False)
  test(one,                  0, BRANCH_BGE_VAL, True)
  test(one,                one, BRANCH_BGE_VAL, True)
  test(max,         max_signed, BRANCH_BGE_VAL, False)
  test(max_signed,         max, BRANCH_BGE_VAL, True)
  test(max,         min_signed, BRANCH_BGE_VAL, True)
  test(min_signed,         max, BRANCH_BGE_VAL, False)
  test(max_signed,  min_signed, BRANCH_BGE_VAL, True)
  test(min_signed,  max_signed, BRANCH_BGE_VAL, False)
  test(max,                max, BRANCH_BGE_VAL, True)


  // BLTU a < b (unsigned)
  test(0,                    0, BRANCH_BLTU_VAL, False)
  test(0,                  one, BRANCH_BLTU_VAL, True)
  test(one,                  0, BRANCH_BLTU_VAL, False)
  test(one,                one, BRANCH_BLTU_VAL, False)
  test(max,         max_signed, BRANCH_BLTU_VAL, False)
  test(max_signed,         max, BRANCH_BLTU_VAL, True)
  test(max,         min_signed, BRANCH_BLTU_VAL, False)
  test(min_signed,         max, BRANCH_BLTU_VAL, True)
  test(max_signed,  min_signed, BRANCH_BLTU_VAL, True)
  test(min_signed,  max_signed, BRANCH_BLTU_VAL, False)
  test(max,                max, BRANCH_BLTU_VAL, False)


  // BGEU a >= (unsigned)
  test(0,                    0, BRANCH_BGEU_VAL, True)
  test(0,                  one, BRANCH_BGEU_VAL, False)
  test(one,                  0, BRANCH_BGEU_VAL, True)
  test(one,                one, BRANCH_BGEU_VAL, True)
  test(max,         max_signed, BRANCH_BGEU_VAL, True)
  test(max_signed,         max, BRANCH_BGEU_VAL, False)
  test(max,         min_signed, BRANCH_BGEU_VAL, True)
  test(min_signed,         max, BRANCH_BGEU_VAL, False)
  test(max_signed,  min_signed, BRANCH_BGEU_VAL, False)
  test(min_signed,  max_signed, BRANCH_BGEU_VAL, True)
  test(max,                max, BRANCH_BGEU_VAL, True)

  // BNOT (not branch)
  test(0,                    0, BRANCH_BNOT_VAL, False)
  test(0,                  one, BRANCH_BNOT_VAL, False)
  test(one,                  0, BRANCH_BNOT_VAL, False)
  test(one,                one, BRANCH_BNOT_VAL, False)
  test(max,         max_signed, BRANCH_BNOT_VAL, False)
  test(max_signed,         max, BRANCH_BNOT_VAL, False)
  test(max,         min_signed, BRANCH_BNOT_VAL, False)
  test(min_signed,         max, BRANCH_BNOT_VAL, False)
  test(max_signed,  min_signed, BRANCH_BNOT_VAL, False)
  test(min_signed,  max_signed, BRANCH_BNOT_VAL, False)
  test(max,                max, BRANCH_BNOT_VAL, False)

  // BJMP (Jump)
  test(0,                    0, BRANCH_BJMP_VAL, True)
  test(0,                  one, BRANCH_BJMP_VAL, True)
  test(one,                  0, BRANCH_BJMP_VAL, True)
  test(one,                one, BRANCH_BJMP_VAL, True)
  test(max,         max_signed, BRANCH_BJMP_VAL, True)
  test(max_signed,         max, BRANCH_BJMP_VAL, True)
  test(max,         min_signed, BRANCH_BJMP_VAL, True)
  test(min_signed,         max, BRANCH_BJMP_VAL, True)
  test(max_signed,  min_signed, BRANCH_BJMP_VAL, True)
  test(min_signed,  max_signed, BRANCH_BJMP_VAL, True)
  test(max,                max, BRANCH_BJMP_VAL, True)

}
