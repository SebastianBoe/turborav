package TurboRav

import Chisel._
import Common._
import Constants._

// This module takes as input the memory requests of the fetch stage
// and the memory stage and arbitrates between them such that RavV
// makes only one memory request at a time.

// With this scheme the fetch and memory stages are agnostic to the
// fact that they are not the sole bus masters. If one of their
// requests is stalled due to bus contention they only see this as an
// abnormally long memory request.

class RavVMemoryRequestArbiter extends Module {
  val io = new Bundle() {
    val ravv = new RequestResponseIo()
    val fch  = new RequestResponseIo().flip()
    val mem  = new RequestResponseIo().flip()
  }

  val s_no_requests :: s_mem :: s_fch :: Nil = Enum(UInt(), 3)
  val state = Reg(init = s_no_requests)
  when(state === s_no_requests){
    when(io.mem.request.valid){
      state := s_mem
    }.elsewhen(io.fch.request.valid){
      state := s_fch
    }
  }.otherwise {
    when(io.ravv.response.valid){
      state := s_no_requests
    }
  }

  val no_response = new ValidIO(new Bundle {
    val word = UInt(width = Config.xlen)
  }).flip()
  no_response.valid     := Bool(false)
  no_response.bits.word := UInt(0)

  // Do it simple with muxes first and then figure out how to do this
  // beautifully later.
  io.ravv.request := Mux(
    state === s_fch,
    io.fch.request,
    io.mem.request
  )

  io.fch.response := Mux(
    state === s_fch,
    io.ravv.response,
    no_response
  )

  io.mem.response := Mux(
    state === s_mem,
    io.ravv.response,
    no_response
  )
}
