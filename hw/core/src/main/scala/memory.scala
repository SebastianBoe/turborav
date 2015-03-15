package TurboRav

import Chisel._
import Common._
import Constants._

/**
  This Memory stage module handles two tasks, apb requests and RAM
  requests. Apb requests are made on io.requestResponseIo, while RAM
  requests are dealt with internally using a RAM module that is
  instantiated within this module.
  */
class Memory extends Module {
  val io = new MemoryIO()

  // Pipeline registers
  val exe_mem = Reg(init = new ExecuteMemory())
  when(!io.i_stall){
    exe_mem := io.exe_mem
  }

  //////////////////////////////////////////////////////////////////////////////
  // Convenience variables
  //////////////////////////////////////////////////////////////////////////////
  val request  = io.requestResponseIo.request
  val response = io.requestResponseIo.response
  val mem_ctrl = exe_mem.mem_ctrl

  //////////////////////////////////////////////////////////////////////////////
  // Common code to APB and RAM requests
  //////////////////////////////////////////////////////////////////////////////
  val is_mem_transfer_instr =
    mem_ctrl.write ||
    mem_ctrl.read

  val is_apb_request = is_mem_transfer_instr && isApbAddress(exe_mem.alu_result)
  val is_ram_request = is_mem_transfer_instr && isRamAddress(exe_mem.alu_result)


  //////////////////////////////////////////////////////////////////////////////
  // RAM request code
  //////////////////////////////////////////////////////////////////////////////
  val ram =  Module(new Ram())
  ram.io.addr   := clearIfDisabled( exe_mem.alu_result , is_ram_request)
  ram.io.word_w := clearIfDisabled( exe_mem.rs2        , is_ram_request)
  ram.io.wen    := mem_ctrl.write && is_ram_request
  val ram_word = ram.io.word_r

  //////////////////////////////////////////////////////////////////////////////
  // APB request code
  //////////////////////////////////////////////////////////////////////////////
  noApbRequest
  // This stage can be in one of two states, either idling, or
  // awaiting a response from the memory hierarchy.
  val s_idle :: s_awaiting_response :: Nil = Enum(UInt(), 2)
  val state = Reg(init = s_idle)
  when(state === s_idle){
    when(is_apb_request){
      state := s_awaiting_response
      doApbRequest
    }.otherwise {
      noApbRequest
    }
  }.otherwise {
    when(response.valid){
      state := s_idle
      noApbRequest
    }.otherwise {
      doApbRequest
    }
  }

  io.mem_wrb.mem_read_data := MuxCase(UInt(0), Array(
    is_ram_request -> ram_word,
    is_apb_request -> response.bits.word
  ))
  io.mem_wrb <> exe_mem
  io.mem_exe.alu_result := exe_mem.alu_result // ??? Why do we feed this back?


  // TODO: Figure out how to resolve load-use hazards.
  io.fwu_mem.rd_wen  := Bool(false)
  io.fwu_mem.rd_addr := UInt(0)

  io.o_stall := is_apb_request && ! response.valid

  def doApbRequest {
    request.valid := Bool(true)
    request.bits.addr := exe_mem.alu_result
    when(mem_ctrl.write) {
      request.bits.wdata := exe_mem.rs2
      request.bits.write := Bool(true)
    }
  }

  def noApbRequest {
    request.valid := UInt(0)
    request.bits.addr := UInt(0)
    request.bits.wdata := UInt(0)
    request.bits.write := UInt(0)
  }
}
