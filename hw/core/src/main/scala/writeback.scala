package TurboRav

import Chisel._
import Common._
import Constants._

class Writeback() extends Module {

  val io = new WritebackIO()

  val mem_wrb = Reg(init = new MemoryWriteback())
  when(!io.stall){
    mem_wrb := io.mem_wrb
  }

  io.wrb_dec.rd_data := mem_wrb.alu_result
  io.wrb_dec.rd_wen := mem_wrb.wrb_ctrl.rd_wen
  io.wrb_dec <> mem_wrb
}