// Copyright (C) 2015 Sebastian Bøe, Joakim Andersson
// License: BSD 2-Clause (see LICENSE for details)

package TurboRav

import Chisel._
import Constants._

class Writeback extends Module {

  val io = new WritebackIO()

  val mem_wrb = Reg(init = new MemoryWriteback())
  unless(io.hdu_wrb.stall){
    mem_wrb := io.mem_wrb
  }
  io.wrb_dec := mem_wrb

  val ctrl = mem_wrb.wrb_ctrl

  val word = Mux(
    ctrl.has_wait_state,
    io.mem_wrb.mem_read_data,
       mem_wrb.mem_read_data
  )

  val half_of_word = UInt(word(16 - 1, 0), width = 16)
  val byte_of_word = UInt(word(8  - 1, 0), width = 8 )

  val mem_read = MuxCase( word, Array(
    ( ctrl.sign_extend && ctrl.is_halfword ) -> SignExtend(half_of_word, 32),
    (!ctrl.sign_extend && ctrl.is_halfword ) -> ZeroExtend(half_of_word, 32),
    ( ctrl.sign_extend && ctrl.is_byte     ) -> SignExtend(byte_of_word, 32),
    (!ctrl.sign_extend && ctrl.is_byte     ) -> ZeroExtend(byte_of_word, 32)
  ))

  val rd_data = Mux(ctrl.rd_sel === RD_PC,  mem_wrb.pc + UInt(4),
                Mux(ctrl.rd_sel === RD_MEM, mem_read,
                                            mem_wrb.alu_result))

  io.fwu_wrb.rd_addr := mem_wrb.rd_addr

  io.wrb_dec.rd_data := rd_data
  io.wrb_exe.rd_data := rd_data
  io.wrb_dec.rd_wen := mem_wrb.wrb_ctrl.rd_wen
  io.fwu_wrb.rd_wen := mem_wrb.wrb_ctrl.rd_wen
}
