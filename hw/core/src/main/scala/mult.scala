package TurboRav

import Chisel._

class Mult(val xlen: Int) extends Module with Constants {

    val io = new Bundle(){
        // multiplicand, dividend
        val inA    = UInt(INPUT, xlen)
        // multiplier, divisor
        val inB    = UInt(INPUT, xlen)
        val enable = Bool(INPUT)
        val abort  = Bool(INPUT)
        val func   = UInt(INPUT, 3)

        val outL = UInt(OUTPUT, xlen)
        val outH = UInt(OUTPUT, xlen)
        val done = Bool(OUTPUT)

    }

    def isDivide(func: UInt) = func(2)

    /* For some reason i cannot name these with uppercase */
    val s_idle:: s_mult :: s_div :: s_done :: Nil = Enum(UInt(), 4)

    val state     = Reg(init = s_idle)
    val exec_func = Reg(UInt())
    val count = Reg(UInt(width = log2Up(xlen)))

    val shift_right = Bool()
    val shift_left  = Bool()
    val write       = Bool()

    // Holds the product, or the combined quotient and remainder
    // plus one for overflow from the adder during computation
    val holding = Reg(UInt(xlen * 2 + 1))

    // Holds the multiplicand or the divisor
    val argument = Reg(UInt(xlen))

    val sum = holding(xlen*2, xlen) + Mux(isDivide(exec_func), -argument, argument)

    val next_holding = Mux(holding(0) === UInt(1),
                        Cat(sum, holding(xlen-1, 0)),
                        holding)

    when(state === s_idle && io.enable){
        when(isDivide(io.func)){
            state := s_div
            //holding := Cat(UInt(0, width = xlen + 1), io.inA)
        } .otherwise {
            state := s_mult
            argument := io.inA
            holding := Cat(UInt(0, width = xlen + 1), io.inB)
        }
        exec_func := io.func
        count := UInt(0)
    }

    when(state === s_div){
        state := s_done
        when(count === UInt(xlen)){
            state := s_idle
        }
        count := count + UInt(1)
    }

    when(state === s_mult){
        when(count === UInt(xlen-1)){
           state := s_idle
        }

        holding := next_holding >> UInt(1)
        count := count + UInt(1)
    }

    when(state === s_done){
        state := s_idle
    }

    io.outH := holding((xlen * 2) - 1, xlen)
    io.outL := holding(xlen - 1, 0)
    io.done := state === s_idle

}