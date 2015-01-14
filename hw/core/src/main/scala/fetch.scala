package TurboRav

import Chisel._
import Common._
import Constants._

class Fetch() extends Module {
  val io = new FetchIO()

  val rr = io.requestResponseIo // For convenience

  val pc = Reg(init = UInt(0, width = Config.xlen))
  val pc_next = pc + UInt(4)

  val should_stall = io.i_stall || !rr.response.valid
  when(!should_stall){
    pc := pc_next
  }

  // Fetch to decode
  io.fch_dec.pc          := pc
  io.fch_dec.instr_valid := rr.response.valid
  io.fch_dec.instr       := rr.response.bits.word

  // Memory interface to fetch
  rr.request.bits.addr := pc
  rr.request.valid := Bool(true) // I think this is safe.
}
