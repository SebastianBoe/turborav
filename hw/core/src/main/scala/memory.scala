package TurboRav

import Chisel._
import Common._
import Constants._

class Memory extends Module {
  val io = new MemoryIO()

  // Pipeline registers
  val exe_mem = Reg(init = new ExecuteMemory())

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
  request.bits.bytes := mem_ctrl.mem_width

  unless(io.hdu_mem.stall) {
    exe_mem := io.exe_mem
  }

  io.mem_wrb.mem_read_data := response.bits.word
  io.mem_wrb <> exe_mem

  // Forwarding of ALU result
  io.mem_exe.alu_result := exe_mem.alu_result

  io.fwu_mem.rd_wen  := exe_mem.wrb_ctrl.rd_wen
  io.fwu_mem.rd_addr := exe_mem.rd_addr

  io.hdu_mem.mem_read := mem_ctrl.read
  io.hdu_mem.rd_addr  := exe_mem.rd_addr

  io.o_stall := mem_ctrl.read
}
