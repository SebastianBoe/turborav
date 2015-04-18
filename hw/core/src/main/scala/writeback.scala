package TurboRav

import Chisel._
import Common._
import Constants._

class Writeback() extends Module {

  val io = new WritebackIO()

  val mem_wrb = Reg(init = new MemoryWriteback())
  unless(io.hdu_wrb.stall){
    mem_wrb := io.mem_wrb
  }

  val ctrl = mem_wrb.wrb_ctrl
  val rd_data = Mux(ctrl.rd_sel === RD_PC,  mem_wrb.pc + UInt(4),
                Mux(ctrl.rd_sel === RD_MEM, mem_wrb.mem_read_data,
                                            mem_wrb.alu_result))

  io.fwu_wrb.rd_addr := mem_wrb.rd_addr

  io.wrb_dec.rd_data := rd_data
  io.wrb_exe.rd_data := rd_data
  io.wrb_dec.rd_wen := mem_wrb.wrb_ctrl.rd_wen
  io.fwu_wrb.rd_wen := mem_wrb.wrb_ctrl.rd_wen
  io.wrb_dec <> mem_wrb
}
