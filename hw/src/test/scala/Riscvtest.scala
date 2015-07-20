package TurboRav

import Chisel._
import scala.math.BigInt
import scala.xml.PrettyPrinter
import org.apache.commons.io.FileUtils
import java.io.File

/**
  A testbench for simulating tests in the riscv-tests repo.

  The testbench knows how riscv-tests report test pass and test
  failure and is able to stop the test execution once a test pass or
  failure has been detected.
  */
class RiscvTest(c: Soc, test_name: String) extends Tester(c, isTrace = false) {
  while(getTestStatus() == Running)
  {
    step(1)
  }
  expect(getTestStatus() == Passed, "")

  val error_msg = "Failed test #%d".format( peekAt(c.ravv.dec.regbank.regs, 28 ) )
  generateXmlForJenkins(error_msg)

  println("")
  printRegs()
  if(!ok) println(error_msg)
  println("")

  private def getTestStatus() : TestStatus = {
    val TestPassInstr = ScalaUtil.hex2dec("51e0d073")
    val TestFailInstr = ScalaUtil.hex2dec("51ee1073")
    peek(c.ravv.dec.fch_dec.instr) match {
      case TestPassInstr => Passed
      case TestFailInstr => Failed
      case _ => Running
    }
  }

  private def printRegs() {
    // Names used by the toolchain, not the RISC V ISA spec.
    val regs_canonical = Array(
      "zero",
      "ra",
      "sp",
      "gp",
      "tp",
      "s0", "s1",
      "t0", "t1", "t2",
      "a0", "a1", "a2", "a3", "a4", "a5", "a6", "a7",
      "s2", "s3", "s4", "s5", "s6", "s7", "s8", "s9", "s10", "s11",
      "t3", "t4", "t5", "t6"
    )
    val regs = Array.ofDim[BigInt](32)

    for(i <- 0 until 32){
      regs(i) = peekAt(c.ravv.dec.regbank.regs, i)
    }

    println("Register bank:")
    for(i <- 0 until 32 / 4){
      for(j <- 0 until 4){
        val x = i + (32/4)*j
        print("\t%5s (x%02d): %08x".format(regs_canonical(x), x, regs(x)))
      }
      println("")
    }
    println("")
  }

  private def generateXmlForJenkins(error_msg: String) {
    val file_name = "%s/jenkins.xml" format(test_name)
    val file_contents = new PrettyPrinter(80, 2).format(
      <testsuite>
        <testcase classname={test_name}>
        {if (ok) "" else <failure type="a_type">{error_msg}</failure>}
        </testcase>
      </testsuite>
    ) + "\n"

    FileUtils.writeStringToFile(
      new File(file_name),
      file_contents
    )
  }
}


trait TestStatus
case object Running  extends TestStatus
trait Finished extends TestStatus

case object Failed   extends Finished
case object Passed   extends Finished
