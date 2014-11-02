package TurboRav

import Chisel._
import Common._
import Constants._

// The purely combinatorial Arithmetic Logic Unit.

class Alu (implicit conf: TurboravConfig) extends Module {

  require(isPow2(conf.xlen))

  val io = new Bundle {
    val func = UInt(INPUT, 4)
    val inA  = UInt(INPUT, conf.xlen)
    val inB  = UInt(INPUT, conf.xlen)
    val out  = UInt(OUTPUT, conf.xlen)
  }
  // The value given when no valid func is applied.
  val defaultAluOutput = UInt(0, conf.xlen)

  val shamt = UInt(
    io.inB(log2Up(conf.xlen) - 1, 0),
    width = log2Up(conf.xlen)
  )

  io.out := Lookup(
    io.func,
    defaultAluOutput,
    Array(
      ALU_ADD  -> (io.inA + io.inB),
      ALU_SUB  -> (io.inA - io.inB),
      ALU_SLT  -> (io.inA.toSInt() < io.inB.toSInt()),
      ALU_SLTU -> (io.inA < io.inB),
      ALU_AND  -> (io.inA & io.inB),
      ALU_OR   -> (io.inA | io.inB),
      ALU_XOR  -> (io.inA ^ io.inB),
      ALU_SLL  -> (io.inA << shamt),
      ALU_SRL  -> (io.inA >> shamt),
      ALU_SRA  -> (io.inA.toSInt() >> shamt)
    )
  )
}
