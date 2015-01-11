package TurboRav

import Chisel._
import Common._
import Constants._

class Memory() extends Module {
  val io = new MemoryIO()

  val exe_mem = Reg(init = new ExecuteMemory())
  when(!io.stall){
    exe_mem := io.exe_mem
  }

  val is_mem_transfer_instr =
    io.mem_ctrl.write ||
    io.mem_ctrl.read

  val request  = io.requestResponseIo.request  // For convenience
  val response = io.requestResponseIo.response // For convenience

  // This stage can be in one of two states, either idling, or
  // awaiting a response from the memory hierarchy.
  val s_idle :: s_awaiting_response :: Nil = Enum(UInt(), 2)
  val state = Reg(init = s_idle)
  when(state === s_idle){
    when(is_mem_transfer_instr){
      state             := s_awaiting_response
      request.valid     := Bool(true)
      request.bits.addr := exe_mem.alu_result
      when(io.mem_ctrl.write) {
        request.bits.wdata := exe_mem.rs2
        request.bits.write := Bool(true)
      }
    }
  }.otherwise {
    when(response.valid){
      state              := s_idle
      io.mem_read_data   := response.bits.word
      request.bits.addr  := UInt(0)
      request.bits.wdata := UInt(0)
      request.bits.write := UInt(0)
    }
  }

  io.mem_wrb <> exe_mem

 }
