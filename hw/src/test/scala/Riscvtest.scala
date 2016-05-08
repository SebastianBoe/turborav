package TurboRav

import Chisel._
import scala.math.BigInt

/**
  A testbench for simulating tests in the riscv-tests repo.

  The testbench knows how riscv-tests report test pass and test
  failure and is able to stop the test execution once a test pass or
  failure has been detected.
  */
class RiscvTest(c: Soc, test_name: String)
extends JUnitTester(c, isTrace = false) {

  // Riscvtest creates a file stdout.txt in the directory test_name
  // that contains the simulated FW's printf's.
  val path = test_name + "/stdout.txt"
  val file = new java.io.File(path)
  file.delete() // Delete existing log file
  val stdoutPrintWriter = new java.io.PrintWriter(file)

  while(getTestStatus() == Running)
  {
    possiblySimulatePutchar(stdoutPrintWriter)
    step(1)
  }
  expect(getTestStatus() == Passed, "")

  val error_msg = "Failed test #%d".format( readReg(28) )

  println("")
  printRegs()
  if(!ok) println(error_msg)
  println("")

  stdoutPrintWriter.close()

  private def getTestStatus() : TestStatus = {
    val TestPassInstr = ScalaUtil.hex2dec("51e0d073")
    val TestFailInstr = ScalaUtil.hex2dec("51ee1073")
    getCurrentInstruction() match {
      case TestPassInstr => Passed
      case TestFailInstr => Failed
      case _ => Running
    }
  }

  private def possiblySimulatePutchar(pw: java.io.PrintWriter) {
    if(magicPutcharInstructionFound()) {
      val c = getPutcharFunctionArgument().toChar

      // Write to file
      pw.append(c)

      // Write also to stdout
      print(c)
    }
  }

  private def magicPutcharInstructionFound() = {
    getCurrentInstruction() == ScalaUtil.hex2dec("51ee9073")
  }

  private def getCurrentInstruction() : BigInt = { peek(c.ravv.dec.fch_dec.instr) }

  private def getPutcharFunctionArgument() : BigInt = {
    // The register is 10 because x10 maps to a0, and a0 is where the
    // first argument of a function is stored. See "RISC-V calling
    // convention".
    readReg(10)
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
      regs(i) = readReg(i)
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

  override def getTestName: String = {
    test_name
  }

  private def readReg(i: Int): BigInt = {
    peekAt(c.ravv.exe.regbank.regs, i)
  }
}


trait TestStatus
case object Running  extends TestStatus
trait Finished extends TestStatus

case object Failed   extends Finished
case object Passed   extends Finished
