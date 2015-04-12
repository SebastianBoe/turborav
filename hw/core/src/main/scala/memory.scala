package TurboRav

import Chisel._
import Common._
import Constants._

class Memory extends Module {
  val io = new MemoryIO()

  // Pipeline registers
  val exe_mem = Reg(init = new ExecuteMemory())
  when(!io.i_stall){
    exe_mem := io.exe_mem
  }

  val request  = io.requestResponseIo.request
  val response = io.requestResponseIo.response
  val mem_ctrl = exe_mem.mem_ctrl

  val is_mem_transfer_instr =
    mem_ctrl.write ||
    mem_ctrl.read

  request.valid      := is_mem_transfer_instr
  request.bits.addr  := clearIfDisabled(exe_mem.alu_result, is_mem_transfer_instr)
  request.bits.wdata := exe_mem.rs2
  request.bits.write := mem_ctrl.write

  val ram_word = response.bits.word

  io.mem_wrb.mem_read_data := ram_word
  io.mem_wrb <> exe_mem

  // Forwarding of ALU result
  io.mem_exe.alu_result := exe_mem.alu_result

  io.fwu_mem.rd_wen  := exe_mem.wrb_ctrl.rd_wen
  io.fwu_mem.rd_addr := exe_mem.rd_addr

  // While we have disabled apb there is no way for mem to cause
  // stalls anymore.
  io.o_stall := Bool(false)
}
