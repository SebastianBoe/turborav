// Copyright (C) 2015 Sebastian Bøe, Joakim Andersson
// License: BSD 2-Clause (see LICENSE for details)

package TurboRav

import Chisel._
import Constants._

// A purely combinatorial Arithmetic Logic Unit.

class Alu extends Module {
  val io = new Bundle {
    val func = UInt(INPUT, 4)
    val in_a = UInt(INPUT, Config.xlen)
    val in_b = UInt(INPUT, Config.xlen)
    val out  = UInt(OUTPUT, Config.xlen)
  }

  val DEFAULT_ALU_OUTPUT = UInt(0, Config.xlen)

  val shamt = io.in_b(log2Up(Config.xlen) - 1, 0)

  val add  = io.in_a          +  io.in_b
  val sub  = io.in_a          -  io.in_b
  val sltu = io.in_a          <  io.in_b
  val and  = io.in_a          &  io.in_b
  val or   = io.in_a          |  io.in_b
  val xor  = io.in_a          ^  io.in_b
  val sll  = io.in_a          << shamt
  val srl  = io.in_a          >> shamt
  val slt  = UInt( io.in_a.toSInt() <  io.in_b.toSInt() )
  val sra  = UInt( io.in_a.toSInt() >> shamt )

  io.out := Lookup(
    io.func,
    DEFAULT_ALU_OUTPUT,
    Array(
      ALU_ADD  -> add,
      ALU_SUB  -> sub,
      ALU_SLT  -> slt,
      ALU_SLTU -> sltu,
      ALU_AND  -> and,
      ALU_OR   -> or,
      ALU_XOR  -> xor,
      ALU_SLL  -> sll,
      ALU_SRL  -> srl,
      ALU_SRA  -> sra
    )
  )
}
