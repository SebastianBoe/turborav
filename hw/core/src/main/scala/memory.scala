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
  request.bits.wdata := clearIfDisabled(exe_mem.rs2, mem_ctrl.write)
  request.bits.write := mem_ctrl.write
  request.bits.bytes := mem_ctrl.mem_width

  unless(io.hdu_mem.stall) {
    exe_mem := io.exe_mem

    // In the normal load-use hazard where the exe stage is the user
    // we need a stall no matter what (in an in-order-processor). But
    // not when the mem stage itself is the user, like in memcpy:

    // lw tp,0(sp)
    // sw tp,0(ra)
    //
    // In this case we can forward to ourselves. We do this below.

    exe_mem.rs2 := Mux(mem_ctrl.read, response.bits.word, io.exe_mem.rs2)
  }

  io.mem_wrb.mem_read_data := response.bits.word
  io.mem_wrb <> exe_mem

  // Forwarding of ALU result
  io.mem_exe.alu_result := exe_mem.alu_result

  io.fwu_mem.rd_wen  := exe_mem.wrb_ctrl.rd_wen
  io.fwu_mem.rd_addr := exe_mem.rd_addr

  io.hdu_mem.mem_read := mem_ctrl.read
  io.hdu_mem.rd_addr  := exe_mem.rd_addr

}
