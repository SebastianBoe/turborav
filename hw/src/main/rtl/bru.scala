// Copyright (C) 2015 Sebastian Bøe, Joakim Andersson
// License: BSD 2-Clause (see LICENSE for details)

package TurboRav

import Constants._
import Chisel._

// Purely combinatorial Branch Unit
class BranchUnit() extends Module {

  private def equal(func: Bits) = !func(2) && !func(1)
  private def lt   (func: Bits) =  func(2) && !func(1)
  private def ltu  (func: Bits) =  func(2) &&  func(1)
  private def neg  (func: Bits) =  func(0)

  val io = new Bundle(){
    val in_a = UInt(INPUT, width = Config.xlen)
    val in_b = UInt(INPUT, width = Config.xlen)
    val func = Bits(INPUT, width = BRANCH_FUNC_WIDTH)

    val take = Bool(OUTPUT)
  }

  val take = MuxCase(Bool(false), Array(
      equal(io.func) -> (io.in_a === io.in_b),
      lt   (io.func) -> (io.in_a.toSInt() < io.in_b.toSInt()),
      ltu  (io.func) -> (io.in_a < io.in_b)
      ))

  // BNE, BGE and BGEU are inverse of BEQ, BLT and BLTU respectively
  io.take := take ^ neg(io.func)
}
