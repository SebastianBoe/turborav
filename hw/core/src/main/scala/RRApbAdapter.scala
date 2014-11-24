package TurboRav

import Chisel._
import Common._
import Constants._
import Apb._

// This module implements an adapter from the Request-Response (RR)
// protocol (patent pending) to the apb protocol.

// It exists because the apb protocol is unnecessarily complicated for
// a bus master that simply wants to make a memory request and receive
// a word in response.
class RRApbAdapter extends Module {
  val io = new Bundle (){
    val rr = new RequestResponseIo().flip()
    val apb = new SlaveToApbIo().flip()
  }

  val s_idle :: s_apb_access_phase :: s_apb_transfer_phase :: Nil = Enum(UInt(), 3)
  val state = Reg(init = s_idle)

  when      (state === s_idle) {
    io.rr.response.valid := Bool(false)
    io.rr.response.bits.word  := UInt(0, width = Config.xlen)

    when(io.rr.request.valid){
      // We transition from idling to making a request on the apb bus.
      state            := s_apb_access_phase

      io.apb.sel  := Bool(true)
      io.apb.addr := io.rr.request.bits.addr
    }
  }.elsewhen(state === s_apb_access_phase) {
    state := s_apb_transfer_phase

    io.rr.response.valid := Bool(true)
    io.rr.response.bits.word  := io.apb.rdata
  }.otherwise {
    state := s_idle

    io.rr.response.valid := Bool(false)
    io.rr.response.bits.word  := UInt(0, width = Config.xlen)

    io.apb.addr := UInt(0, width = Config.xlen)
    io.apb.sel := Bool(false)
  }
  io.apb.write := Bool(false)
}
