package TurboRav

import Chisel._
import Constants._

// This module implements an adapter from the Request-Response (RR)
// protocol (patent pending) to the apb protocol.

// It exists because the apb protocol is unnecessarily complicated for
// a bus master that simply wants to issue memory requests.
class RRApbAdapter extends Module {
  val io = new Bundle (){
    val rr = new RequestResponseIo().flip()
    val apb = new SlaveToApbIo().flip()
  }

  val s_idle :: s_apb_access_phase :: s_apb_setup_phase :: Nil = Enum(UInt(), 3)
  val state = Reg(init = s_idle)

  when      (state === s_idle) {
    io.rr.response.valid := Bool(false)
    io.rr.response.bits.word  := UInt(0, width = Config.xlen)

    when(io.rr.request.valid){
      // We transition from idling to making a request on the apb bus.
      state            := s_apb_access_phase

      io.apb.addr  := io.rr.request.bits.addr
      io.apb.wdata := io.rr.request.bits.wdata
      io.apb.write := io.rr.request.bits.write
      io.apb.sel   := Bool(true)
    }
  }.elsewhen(state === s_apb_access_phase) {
    state := s_apb_setup_phase

    io.rr.response.valid := Bool(true)
    io.rr.response.bits.word  := io.apb.rdata
  }.otherwise {
    state := s_idle

    io.rr.response.valid := Bool(false)
    io.rr.response.bits.word  := UInt(0, width = Config.xlen)

    io.apb.addr  := UInt(0, width = Config.apb_addr_len)
    io.apb.wdata := UInt(0, width = Config.apb_data_len)
    io.apb.write := Bool(false)
    io.apb.sel   := Bool(false)
  }
}
