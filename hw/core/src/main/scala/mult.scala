package TurboRav

import Chisel._
import Constants._
import Common._

class Mult() extends Module {
  val xlen = Config.xlen // Extract xlen for convenience
  require(isPow2(xlen))

  val io = new Bundle(){
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

  def is_divide(func: UInt) = func(2)

  def is_signed_divide(func: UInt) = !func(0)

  val s_idle :: s_mult :: s_div :: s_negate_input :: s_negate_output_div :: s_negate_output_mult :: Nil = Enum(UInt(), 6)

  val state     = Reg(init = s_idle)
  val exec_func = Reg(UInt())
  val count     = Reg(UInt(width = log2Up(xlen)))

  val negate                  = Reg(init = Bool(false))
  val dividend_sign           = Reg(init = Bool(false))
  val should_negate_quotient  = Reg(init = Bool(false))

  // Holds the product, or the combined quotient and remainder
  // plus one for overflow from the adder during computation
  val holding = Reg(UInt(xlen * 2 + 1))

  // Holds the multiplicand or the divisor
  val argument = Reg(UInt(xlen))

  // Extend one bit to catch the carry
  val operand_a = Cat(UInt(0, width = 1), holding(xlen*2, xlen))
  val operand_b = Cat(UInt(0, width = 1), Fill(xlen, holding(0)) & argument)

  // Does an implicit right shift
  val next_holding_mult = Cat( operand_a + operand_b, holding(xlen-1, 1))

  val holding_shift = holding << UInt(1)
  val difference = UInt(width = xlen + 1)
  val next_holding_div = UInt(width = 2 * xlen +1)

  difference := holding_shift(xlen*2, xlen) - argument
  next_holding_div := Mux(difference(xlen) === UInt(0),
    Cat(difference, holding_shift(xlen-1, 1), UInt(1, width = 1)),
    holding_shift)

  when (state === s_idle && io.enable) {
    when (is_divide(io.func)) {
      when(is_signed_divide(io.func)){
        state := s_negate_input
        should_negate_quotient := io.in_a(xlen-1) != io.in_b(xlen-1)
        dividend_sign := io.in_a(xlen-1)
      }.otherwise{
        state := s_div
      }
      argument := io.in_b
      holding := Cat(UInt(0, width = xlen + 1), io.in_a)
    } .otherwise {
      state := s_mult
      argument := io.in_a
      holding := Cat(UInt(0, width = xlen + 1), io.in_b)
      negate := (io.in_a(xlen-1) && io.func === MULT_MULHSU)
    }
    exec_func := io.func
    count := UInt(0)
  }

  when (state === s_div) {
    when (count === UInt(xlen-1)) {
      when(is_signed_divide(exec_func)){
        state := s_negate_output_div
      } .otherwise {
        state := s_idle
      }
    }
    holding := next_holding_div
    count := count + UInt(1)
  }

  when(state === s_mult){
    when (count === UInt(xlen-1)) {
      when(negate){
        state := s_negate_output_mult
      }.otherwise {
        state := s_idle
      }
    }
    holding := next_holding_mult
    count := count + UInt(1)
  }

  when(state === s_negate_input){
    state := s_div
    when(argument(xlen-1)){
      argument := -argument
    }
    when(holding(xlen-1)){
      holding(xlen-1, 0) := -holding(xlen-1, 0)
    }
  }

  when(state === s_negate_output_div){
    state := s_idle
    when(dividend_sign != holding((xlen * 2) - 1)){
      holding((xlen * 2) - 1, xlen) := -holding((xlen * 2) - 1, xlen)
    }
    when(should_negate_quotient){
      holding(xlen - 1, 0) := -holding(xlen - 1, 0)
    }
  }

  when(state === s_negate_output_mult){
    state := s_idle
    holding((xlen * 2) - 1, xlen) := -(holding((xlen * 2) - 1, xlen))
    negate := Bool(false)
  }

  when (io.abort) {
    state := s_idle
  }

  io.out_hi := holding((xlen * 2) - 1, xlen)
  io.out_lo := holding(xlen - 1, 0)
  io.done := state === s_idle

}
