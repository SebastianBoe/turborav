package TurboRav

import Chisel._
import scala.math.BigInt

/**
  A testbench for simulating tests in the riscv-tests repo.

  The testbench knows how riscv-tests report test pass and test
  failure and is able to stop the test execution once a test pass or
  failure has been detected.
  */
class RiscvTest(c: Soc) extends Tester(c) {
  while(get_test_status() == Running)
  {
    println(get_test_status())
    step(1)
  }
  print_regs()
  println(
    get_test_status() match { case Failed => "FAILED" case Passed => "PASSED" }
  )

  def get_test_status() : TestStatus = {
    def hex2dec(hex: String): BigInt = {
      hex.toLowerCase().toList.map(
        "0123456789abcdef".indexOf(_)).map(
        BigInt(_)).reduceLeft( _ * 16 + _)
    }

    val TestPassInstr = hex2dec("51e0d073")
    val TestFailInstr = hex2dec("51ee1073")
    println(TestPassInstr)
    println(peek(c.ravv.dec.fch_dec.instr))
    peek(c.ravv.dec.fch_dec.instr) match {
      case TestPassInstr => Passed
      case TestFailInstr => Failed
      case _ => Running
    }
  }

  def print_regs() = {
    val regs = Array.ofDim[BigInt](32)

    for(i <- 0 until 32){
      regs(i) = peekAt(c.ravv.dec.regbank.regs, i)
    }

    print("\n\nRegister bank:\n")
    for(i <- 0 until 32 / 4){
      for(j <- 0 until 4){
        val x= 4*i+j
        print("x%02d: %08x\t".format(x,regs(x)))
      }
      print("\n")
    }
    print("\n\n")
  }
}


trait TestStatus
case object Running  extends TestStatus
trait Finished extends TestStatus

case object Failed   extends Finished
case object Passed   extends Finished
