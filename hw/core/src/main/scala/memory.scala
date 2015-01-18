package TurboRav

import Chisel._
import Common._
import Constants._

class Memory() extends Module {
  val io = new MemoryIO()

  // Pipeline registers
  val exe_mem = Reg(init = new ExecuteMemory())
  when(!io.i_stall){
    exe_mem := io.exe_mem
  }

  // For convenience
  val request  = io.requestResponseIo.request
  val response = io.requestResponseIo.response

  val mem_ctrl = exe_mem.mem_ctrl

  val is_mem_transfer_instr =
    mem_ctrl.write ||
    mem_ctrl.read

  noRequest
  // This stage can be in one of two states, either idling, or
  // awaiting a response from the memory hierarchy.
  val s_idle :: s_awaiting_response :: Nil = Enum(UInt(), 2)
  val state = Reg(init = s_idle)
  when(state === s_idle){
    when(is_mem_transfer_instr){
      state := s_awaiting_response
      doRequest
    }.otherwise {
      noRequest
    }
  }.otherwise {
    when(response.valid){
      state := s_idle
      noRequest
    }.otherwise {
      doRequest
    }
  }

  io.mem_wrb.mem_read_data := response.bits.word
  io.mem_wrb <> exe_mem
  io.o_stall := state === s_awaiting_response

  def doRequest {
    request.valid := Bool(true)
    request.bits.addr := exe_mem.alu_result
    when(mem_ctrl.write) {
      request.bits.wdata := exe_mem.rs2
      request.bits.write := Bool(true)
    }
  }

  def noRequest {
    request.valid := UInt(0)
    request.bits.addr := UInt(0)
    request.bits.wdata := UInt(0)
    request.bits.write := UInt(0)
  }
 }
