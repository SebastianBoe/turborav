// Copyright (C) 2015 Sebastian Bøe, Joakim Andersson
// License: BSD 2-Clause (see LICENSE for details)

package TurboRav

import Chisel._
import Constants._

// The purely combinatorial Arithmetic Logic Unit.

class Alu () extends Module {

  require(isPow2(Config.xlen))

  val io = new Bundle {
    val func = UInt(INPUT, 4)
    val in_a  = UInt(INPUT, Config.xlen)
    val in_b  = UInt(INPUT, Config.xlen)
    val out  = UInt(OUTPUT, Config.xlen)
  }

  val shamt = UInt(
    io.in_b(log2Up(Config.xlen) - 1, 0),
    width = log2Up(Config.xlen)
  )

  io.out := Lookup(
    io.func,
    UInt(0, Config.xlen),
    Array(
      ALU_ADD  -> (io.in_a + io.in_b),
      ALU_SUB  -> (io.in_a - io.in_b),
      ALU_SLT  -> (io.in_a.toSInt() < io.in_b.toSInt()).toUInt,
      ALU_SLTU -> (io.in_a < io.in_b),
      ALU_AND  -> (io.in_a & io.in_b),
      ALU_OR   -> (io.in_a | io.in_b),
      ALU_XOR  -> (io.in_a ^ io.in_b),
      ALU_SLL  -> (io.in_a << shamt),
      ALU_SRL  -> (io.in_a >> shamt),
      ALU_SRA  -> (io.in_a.toSInt() >> shamt).toUInt
    )
  )
}
