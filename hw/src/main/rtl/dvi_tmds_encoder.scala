// Copyright (C) 2015 Sebastian Bøe, Joakim Andersson
// License: BSD 2-Clause (see LICENSE for details)

package TurboRav

import Chisel._
import Constants._

// This class implements the flow chart on page 29 in the DVI spec
// V1.0. This code is impossible to understand without the spec. and
// hard to understand with it!

// The code is optimized to be obviously matching the spec. on page
// 29, not to be obviously correct by itself.
class dvi_tmds_encoder extends Module {
  val io = new Bundle {
    val d     = UInt(INPUT, 8)
    val c     = UInt(INPUT, 2)
    val de    = Bool(INPUT)
    val cnt   = SInt(INPUT, 4)

    val q_out    = UInt(OUTPUT, 10)
    val cnt_next = SInt(OUTPUT, 4)
  }

  val majority = ((N1(io.d)  >  UInt(4))) ||
                  (N1(io.d) === N0(io.d) && (io.d(0) === UInt(0)))

  val q_m_vec  = Vec.fill(9){ Wire(Bool()) }
  q_m_vec(0) := io.d(0)
  q_m_vec(8) := !majority

  for (i <- 1 to 7)
    q_m_vec(i) := Mux(
      majority,
      !(q_m_vec(i-1) ^ io.d(i)),
       (q_m_vec(i-1) ^ io.d(i))
    )
  val q_m = q_m_vec.toBits
  val q_m_0_7 = q_m(7,0)
  when(io.de) {
    when(io.cnt === UInt(0) || N1(q_m_0_7) === N0(q_m_0_7)) {
      io.q_out := Cat(
        ~q_m(8),
         q_m(8),
        Mux(q_m(8), q_m_0_7, ~q_m_0_7)
      )
      when(q_m(8) === UInt(0)) {
        io.cnt_next := io.cnt + (N0(q_m_0_7) - N1(q_m_0_7))
      }.otherwise {
        io.cnt_next := io.cnt + (N1(q_m_0_7) - N0(q_m_0_7))
      }
    }.otherwise {
      when(
        io.cnt > UInt(0) && N1(q_m_0_7) > N0(q_m_0_7)
          ||
        io.cnt < UInt(0) && N0(q_m_0_7) > N1(q_m_0_7)
      ) {
        io.q_out := Cat(
          UInt(1),
          q_m(8),
          ~q_m_0_7
        )
        io.cnt_next := io.cnt + (q_m(8) << UInt(1)) +
          (N0(q_m_0_7) - N1(q_m_0_7))
      }.otherwise {
        io.q_out := Cat(
          UInt(0),
          q_m(8),
          q_m_0_7
        )
        io.cnt_next := io.cnt - (~q_m(8) << UInt(1)) +
          (N1(q_m_0_7) - N0(q_m_0_7))
      }
    }
  }.otherwise {
    io.q_out    := encode_ctrl(io.c)
    io.cnt_next := UInt(0)
  }

  private def encode_ctrl(c : UInt): UInt = {
    val ctrl_codes = Vec(
      UInt("b0010101011"),
      UInt("b1101010100"),
      UInt("b0010101010"),
      UInt("b1101010101")
    )
    return ctrl_codes(c)
  }

  private def N1(q_m : UInt) = PopCount( q_m)
  private def N0(q_m : UInt) = PopCount(~q_m)

}
