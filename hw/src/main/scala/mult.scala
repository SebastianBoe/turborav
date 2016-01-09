// Copyright (C) 2015 Sebastian Bøe, Joakim Andersson
// License: BSD 2-Clause (see LICENSE for details)

package TurboRav

import Chisel._
import Constants._

class Mult extends Module {
  val xlen = Config.xlen // Extract xlen for convenience
  require(isPow2(xlen))

  val io = new Bundle {
    // multiplicand, dividend
    val in_a    = UInt(INPUT, xlen)
    // multiplier, divisor
    val in_b    = UInt(INPUT, xlen)
    val enable = Bool(INPUT)
    val abort  = Bool(INPUT)
    val func   = UInt(INPUT, MULT_FUNC_WIDTH)

    val out_lo = UInt(OUTPUT, xlen)
    val out_hi = UInt(OUTPUT, xlen)
    val done = Bool(OUTPUT)
  }

  private def isDivide(func: UInt) = func(2)

  private def isSignedDivide(func: UInt) = !func(0)

  private def isSignedMult(func: UInt) =
    func === MULT_MULH ||
    func === MULT_MULHSU

  val (
    s_idle ::
    s_mult ::
    s_div  ::
    s_negate_input ::
    s_negate_output_div ::
    s_negate_output_mult ::
    Nil
  ) = Enum(UInt(), 6)

  val state     = Reg(init = s_idle)
  val exec_func = Reg(UInt())
  val count     = Reg(UInt(width = log2Up(xlen)))

  val should_negate_product   = Reg(init = Bool(false))
  val dividend_sign           = Reg(init = Bool(false))
  val should_negate_quotient  = Reg(init = Bool(false))

  // Holds the product, or the combined quotient and remainder
  // plus one for overflow from the adder during computation
  val holding    = Reg(UInt(width = 2 * xlen + 1))

  // Holds the multiplicand or the divisor
  val argument = Reg(UInt(width = xlen))

  // Extend one bit to catch the carry
  val operand_a = ZeroExtend(holding(2 * xlen, xlen)          , new_length = xlen + 1)
  val operand_b = ZeroExtend(argument & Fill(xlen, holding(0)), new_length = xlen + 1)

  // Does an implicit right shift
  val next_holding_mult = Cat( operand_a + operand_b, holding(xlen-1, 1))

  val holding_shift = holding << UInt(1)
  val next_holding_div = UInt(width = 2 * xlen + 1)

  val difference = UInt(
    holding_shift(2 * xlen, xlen) - argument,
    width = xlen + 1
  )
  next_holding_div := Mux(
    difference(xlen) === UInt(0),
    Cat(difference, holding_shift(xlen-1, 1), UInt(1)),
    holding_shift
  )

  when (state === s_idle && io.enable) {
    when (isDivide(io.func)) {
      when(isSignedDivide(io.func)){
        state                  := s_negate_input
        should_negate_quotient := io.in_a(xlen-1) =/= io.in_b(xlen-1)
        dividend_sign          := io.in_a(xlen-1)
      }.otherwise {
         // Unsigned
        state := s_div
      }
      argument := io.in_b
      holding := Cat(UInt(0, width = xlen + 1), io.in_a)
    } .otherwise {
      when(isSignedMult(io.func)){
        state := s_negate_input
      } .otherwise {
        state := s_mult
      }
      argument := io.in_a
      holding := Cat(UInt(0, width = xlen + 1), io.in_b)
      should_negate_product := ((io.in_a(xlen-1)
                                && io.func === MULT_MULHSU)
                            || ((io.in_a(xlen-1) =/= io.in_b(xlen-1))
                                && io.func === MULT_MULH))
    }
    exec_func := io.func
    count := UInt(0)
  }

  when (state === s_div) {
    val end_of_div = count === UInt(xlen-1)
    when (end_of_div) {
      state := Mux(isSignedDivide(exec_func), s_negate_output_div, s_idle)
    }
    holding := next_holding_div
    count := count + UInt(1)
  }

  when(state === s_mult){
    val end_of_mult = count === UInt(xlen-1)
    when (end_of_mult) {
      state := Mux(should_negate_product, s_negate_output_mult, s_idle)
    }
    holding := next_holding_mult
    count := count + UInt(1)
  }

  when(state === s_negate_input){
    state := Mux(isDivide(exec_func), s_div, s_mult)
    when(argument(xlen-1)){
      argument := -argument
    }
    when(holding(xlen-1) && exec_func =/= MULT_MULHSU){
      holding(xlen-1, 0) := -holding(xlen-1, 0)
    }
  }

  when(state === s_negate_output_div){
    state := s_idle
    when(dividend_sign =/= holding(2 * xlen - 1)){
      holding(2 * xlen - 1, xlen) := -holding(2 * xlen - 1, xlen)
    }
    when(should_negate_quotient){
      holding(xlen - 1, 0) := -holding(xlen - 1, 0)
    }
  }

  when(state === s_negate_output_mult){
    state := s_idle

    // When the lower xlen bits of the result is zero, we will get
    // a carry from doing twos complement on the lower xlen bits.
    // This implies that negating the upper bits of the product
    // involves twos complement when the lower bits are zero, otherwise invert.
    when(holding(xlen-1,0) === UInt(0)){
      holding((xlen * 2) - 1, xlen) := ~(holding((xlen * 2)-1, xlen)) + UInt(1)
    } .otherwise {
      holding((xlen * 2) - 1, xlen) := ~(holding((xlen * 2)-1, xlen))
    }
  }

  when (io.abort) {
    state := s_idle
  }

  io.out_hi := holding(2 * xlen - 1, xlen)
  io.out_lo := holding(xlen - 1, 0)
  io.done   := state === s_idle
}
