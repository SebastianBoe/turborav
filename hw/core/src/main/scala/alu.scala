package TurboRav

import Chisel._
import Common._
import Constants._

// The purely combinatorial Arithmetic Logic Unit.

class Alu (implicit conf: TurboravConfig) extends Module {

  require(isPow2(conf.xlen))

  val io = new Bundle {
    val func = UInt(INPUT, 4)
    val in_a  = UInt(INPUT, conf.xlen)
    val in_b  = UInt(INPUT, conf.xlen)
    val out  = UInt(OUTPUT, conf.xlen)
  }

  val shamt = UInt(
    io.in_b(log2Up(conf.xlen) - 1, 0),
    width = log2Up(conf.xlen)
  )

  io.out := Lookup(
    io.func,
    UInt(0, conf.xlen),
    Array(
      ALU_ADD  -> (io.in_a + io.in_b),
      ALU_SUB  -> (io.in_a - io.in_b),
      ALU_SLT  -> (io.in_a.toSInt() < io.in_b.toSInt()),
      ALU_SLTU -> (io.in_a < io.in_b),
      ALU_AND  -> (io.in_a & io.in_b),
      ALU_OR   -> (io.in_a | io.in_b),
      ALU_XOR  -> (io.in_a ^ io.in_b),
      ALU_SLL  -> (io.in_a << shamt),
      ALU_SRL  -> (io.in_a >> shamt),
      ALU_SRA  -> (io.in_a.toSInt() >> shamt)
    )
  )
}
