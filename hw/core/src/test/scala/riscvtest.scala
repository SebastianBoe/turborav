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
  while(get_test_status() == Running)
  {
    step(1)
  }
  print_regs()
  generate_xml_for_jenkins()

  def get_test_status() : TestStatus = {
    def hex2dec(hex: String): BigInt = {
       // There has to be a better way to do this in scala, i don't
       // understand why BigInt("81923ba", 16) didn't work.
      hex.toLowerCase().toList.map(
        "0123456789abcdef".indexOf(_)).map(
        BigInt(_)).reduceLeft( _ * 16 + _)
    }

    val TestPassInstr = hex2dec("51e0d073")
    val TestFailInstr = hex2dec("51ee1073")
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

  def generate_xml_for_jenkins() {
    val file_name = "generated/%s.xml" format(test_name)
    val file_contents = new PrettyPrinter(80, 2).format(
      <testsuite>
        <testcase classname="a_classname" name={test_name}>
        {if (ok) "" else <failure type="a_type">error_msg</failure>}
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
