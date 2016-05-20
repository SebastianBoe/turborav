// Copyright (C) 2015 Sebastian Bøe, Joakim Andersson
// License: BSD 2-Clause (see LICENSE for details)

package TurboRav

import Chisel._
import Constants._

/*
 * The memory stage handles memory request related logic.
 *
 * The main pipeline delay of this stage comes from the synchronous
 * ram module. To not double-pipeline this stage we send memory
 * request signals asynchrounously through io.rr_io.request.
 *
 * The response comes back 1 clock cycle later through
 * io.rr_io.response.
 * */

class Memory extends Module {
  val io = new MemoryIO()

  // Pipeline registers
  val exe_mem = Reg(init = new ExecuteMemory())

  val request  = io.rr_io.request
  val response = io.rr_io.response
  val mem_ctrl = io.exe_mem.mem_ctrl

  val stall_mem = Wire(Bool())

  // 1 bit of state for keeping track of if we are doing a multi-cycle
  // memory transfer like a MMIO access.
  val is_multi_cycle_transfer_next = Wire(Bool())
  val is_multi_cycle_transfer = Reg(init = Bool(false), next = is_multi_cycle_transfer_next)

  // Usually we comb. use control signals from execute to do memory
  // requests to Roam, but there is an exception. During a multi-cycle
  // memory transfer we must first use control signals from exe as
  // usual, but on the next clock cycle exe will be giving us
  // different control signals. To hold the same memory request over
  // several cycles we we switch to using signals from the pipeline
  // register on the second cycle.
  val ctrl = Mux(
    is_multi_cycle_transfer,
    exe_mem,
    io.exe_mem
  )

  val is_mem_transfer = ctrl.mem_ctrl.write || ctrl.mem_ctrl.read
  request.valid        := is_mem_transfer
  request.bits.addr    := ctrl.alu_result
  request.bits.wdata   := ctrl.rs2
  request.bits.write   := ctrl.mem_ctrl.write
  request.bits.byte_en := Cat(ctrl.mem_ctrl.is_halfword, ctrl.mem_ctrl.is_byte)

  when(is_multi_cycle_transfer) {
    // During a mulit-cycle transfer, we stay in this state until we
    // get a valid response.
    is_multi_cycle_transfer_next := ! response.valid
  }.elsewhen(is_mem_transfer && isApbAddress(request.bits.addr)) {
    is_multi_cycle_transfer_next := Bool(true)
  }.otherwise {
    is_multi_cycle_transfer_next := Bool(false)
  }

  io.mem_wrb := exe_mem

  io.mem_wrb.mem_read_data := response.bits.word

  val alu_result_or_mult_result = Mux(
    exe_mem.mult.valid,
    exe_mem.mult.bits.result,
    exe_mem.alu_result
  )

  io.mem_wrb.pc_next_or_alu_result_or_mult_result := Mux(
    exe_mem.wrb_ctrl.rd_sel === RD_PC,
    exe_mem.pc_next,
    alu_result_or_mult_result
  )

  // Forwarding of either the ALU result or the MULT result
  io.mem_exe.alu_result_or_mult_result := alu_result_or_mult_result

  io.fwu_mem.rd_wen  := exe_mem.wrb_ctrl.rd_wen
  io.fwu_mem.rd_addr := exe_mem.rd_addr

  io.hdu_mem.mem_read := exe_mem.mem_ctrl.read
  io.hdu_mem.rd_addr  := exe_mem.rd_addr
  io.hdu_mem.mem_busy := is_multi_cycle_transfer

  assert(
    ! io.exe_mem.mem_ctrl.read ||
      io.exe_mem.mem_ctrl.read && io.exe_mem.wrb_ctrl.rd_wen,
    """A read was requested from mem without writeback also getting
    control signals for writing the memory value into the register
    bank."""
  )

  // Create a bubble when stalling, unless we get a valid response
  when(is_multi_cycle_transfer && ! io.rr_io.response.valid) {
    io.mem_wrb.kill()
  }

  // Don't update pipeline registers while stalling
  unless(is_multi_cycle_transfer && ! io.rr_io.response.valid) {
    exe_mem := io.exe_mem
  }
}
