package TurboRav

import Chisel._
import Constants._

class MemoryTest(c: Memory) extends JUnitTester(c) {
  val apb_address = 0x20000000
  val ram_address = 0x10000000
  val rom_address = 0x00000000

  def reset_all() = {
    step(1)
    clear_all_pokes()
    reset(1)
    step(1)
  }

  def clear_all_pokes() = {
    // NB: Must add all the poke values used in the tb here.
    // (This really sucks, how to do better? Let us hope testing is
    // better in Chisel3)
    poke(c.io.exe_mem.wrb_ctrl.rd_wen, 0)
    poke(c.io.exe_mem.mem_ctrl.read, 0)
    poke(c.io.exe_mem.alu_result, 0)
    poke(c.io.rr_io.response.valid, 0)
    poke(c.io.rr_io.response.bits.word, 0)
    poke  (c.io.exe_mem.wrb_ctrl.sign_extend, 0)
    poke(c.io.exe_mem.wrb_ctrl.rd_sel, 0)
  }

  println("The mem stage will pipeline control signals")
  poke  (c.io.exe_mem.wrb_ctrl.rd_wen, 1)
  expect(c.io.mem_wrb.wrb_ctrl.rd_wen, 0) // Don't pass control signals through immediately
  step(1)
  expect(c.io.mem_wrb.wrb_ctrl.rd_wen, 1) // Pass control signals through after 1 clock cycle

  reset_all()

  // Temporarily commented out because we are not going to do this any more I think,
  // delete when sure.
  // println("When mem is stalled, it will retain it's control signals until freed")

  // val ctrl_signal_input  = c.io.exe_mem.wrb_ctrl.sign_extend
  // val ctrl_signal_output = c.io.mem_wrb.wrb_ctrl.sign_extend

  // poke  (ctrl_signal_input, 1) // Original control signal

  // step(1)
  // poke  (ctrl_signal_input, 0) // New control signal that should be ignored
  // poke  (c.io.hdu_mem.stall, 1) // Stall and retain original signal

  // step(1)
  // expect(ctrl_signal_output, 0)

  // step(1)
  // expect(ctrl_signal_output, 0)
  // poke  (c.io.hdu_mem.stall, 0)
  // expect(ctrl_signal_output, 1)

  // step(1)
  // expect(ctrl_signal_output, 0)

  reset_all()

  println("Read control signals will comb. cause a memory request")
  poke  (c.io.exe_mem.mem_ctrl.read, 1)
  poke  (c.io.exe_mem.wrb_ctrl.rd_wen, 1)
  expect(c.io.rr_io.request.valid, 1)

  reset_all()

  println("RAM read scenario")
  poke  (c.io.exe_mem.wrb_ctrl.rd_wen, 1)
  poke  (c.io.exe_mem.mem_ctrl.read, 1)
  poke  (c.io.exe_mem.alu_result, ram_address)
  expect(c.io.rr_io.request.valid, 1)
  step(1) // Roam responds 1 cycle later with a valid flag and a word
  poke  (c.io.rr_io.response.valid, 1)
  poke  (c.io.rr_io.response.bits.word, 42)
  expect(c.io.mem_wrb.mem_read_data, 42)

  reset_all()

  // I don't think anything can stall mem, so doesn't make sense to
  // test it.
  // println("Create a bubble on stall")
  // poke  (c.io.exe_mem.alu_result, ram_address)
  // step(1) // Roam responds 1 cycle later with a valid flag and a word
  // poke  (c.io.hdu_mem.stall, 1)
  // expect(c.io.mem_wrb.alu_result, 0)

  reset_all()

  println("Multi-cycle MMIO read scenario")
  poke  (c.io.exe_mem.wrb_ctrl.rd_wen, 1)
  poke  (c.io.exe_mem.mem_ctrl.read, 1)
  poke  (c.io.exe_mem.alu_result, apb_address)
  expect(c.io.rr_io.request.valid, 1)
  expect(c.io.hdu_mem.mem_busy, 0)
  step(1) // Roam does not give a valid response after the first cycle

  // exe sends a new instruction to mem, which should be ignored by mem
  poke  (c.io.exe_mem.wrb_ctrl.rd_wen, 0)
  poke  (c.io.exe_mem.mem_ctrl.read, 0)
  poke  (c.io.exe_mem.alu_result, ram_address)

  expect(c.io.mem_wrb.wrb_ctrl.rd_wen, 0,
         """
         mem is doing a multi-cycle memory transfer now so mem
         should send bubbles down the pipeline until the operation is
         done.
         """
       )

  expect(c.io.hdu_mem.mem_busy, 1, "Mem should signal that it is busy")

  // The memory request signals are a part of the multi-cycle
  // operation and should therefore not be bubbled like wrb's rd_wen
  // control signal, but instead be held until the transfer is
  // complete.
  expect(c.io.rr_io.request.valid, 1)

  step(1) // After 1 busy-cycle the APB request goes through
  poke  (c.io.rr_io.response.valid, 1)
  poke  (c.io.rr_io.response.bits.word, 21)

  expect(c.io.hdu_mem.mem_busy, 1, "Mem should keep the busy signal for one last cycle")

  expect(c.io.mem_wrb.wrb_ctrl.rd_wen, 1)
  expect(c.io.mem_wrb.mem_read_data, 21)

  step(1)
  poke  (c.io.rr_io.response.valid, 0)
  poke  (c.io.rr_io.response.bits.word, 0)

  expect(c.io.hdu_mem.mem_busy, 0, "Mem should no longer stall the pipeline.")

  expect(c.io.mem_wrb.wrb_ctrl.rd_wen, 0)

  // println("Multi-cycle MMIO read scenario")
  // poke  (c.io.exe_mem.wrb_ctrl.rd_wen, 1)
  // poke  (c.io.exe_mem.mem_ctrl.read, 1)
  // poke  (c.io.exe_mem.alu_result, apb_address)
  // expect(c.io.rr_io.request.valid, 1)
  // expect(c.io.hdu_mem.mem_busy, 0)
  // step(1) // Roam does not give a valid response after the first cycle

  // // exe sends a new instruction to mem, which should be ignored by mem
  // poke  (c.io.exe_mem.wrb_ctrl.rd_wen, 0)
  // poke  (c.io.exe_mem.mem_ctrl.read, 0)
  // poke  (c.io.exe_mem.alu_result, ram_address)

  // // HDU should have been signaled that we are doing a multi-cycle
  // // operation, and should therefore signal to the mem stage that it
  // // should stall.
  // poke(c.io.hdu_mem.stall, 1)

  // expect(c.io.mem_wrb.wrb_ctrl.rd_wen, 0,
  //        """
  //        mem is doing a multi-cycle memory transfer now so mem
  //        should send bubbles down the pipeline until the operation is
  //        done.
  //        """
  //      )

  // // The memory request signals are a part of the multi-cycle
  // // operation and should therefore not be bubbled like wrb's rd_wen
  // // control signal, but instead be held until the transfer is
  // // complete.
  // expect(c.io.rr_io.request.valid, 1)

  // expect(c.io.hdu_mem.mem_busy, 1)

  // step(1) // After 1 busy-cycle the APB request goes through
  // poke  (c.io.rr_io.response.valid, 1)
  // poke  (c.io.rr_io.response.bits.word, 21)

  // // Mem should no longer signal that it is busy.
  // expect(c.io.hdu_mem.mem_busy, 0)
  // // And in comb. response, HDU should stop stalling mem.
  // poke(c.io.hdu_mem.stall, 0)

  // expect(c.io.mem_wrb.wrb_ctrl.rd_wen, 1)
  // expect(c.io.mem_wrb.alu_result, apb_address)
  // expect(c.io.mem_wrb.mem_read_data, 21)

  reset_all()

  println("Mem will signal a stall when doing a multi-cycle memory transfer")
  poke  (c.io.exe_mem.wrb_ctrl.rd_wen, 1)
  poke  (c.io.exe_mem.mem_ctrl.read, 1)
  poke  (c.io.exe_mem.alu_result, apb_address)
  expect(c.io.hdu_mem.mem_busy, 0,
         """Mem should not signal to the HDU that it is busy
         comb. after getting multi-cycle memory transfer signals, it
         should instead wait until the request has been written to the
         pipeline registers. This is to make the stall signaling
         consistent with other stages."""
       )
  step(1)
  poke  (c.io.exe_mem.wrb_ctrl.rd_wen, 0)
  poke  (c.io.exe_mem.mem_ctrl.read, 0)
  poke  (c.io.exe_mem.alu_result, 0)
  expect(c.io.hdu_mem.mem_busy, 1,
         """We now have the MMIO transfer control signals in the mem
         pipeline registers and can start signaling that we need to
         stall."""
       )
  step(1)
  expect(c.io.hdu_mem.mem_busy, 1)
  poke  (c.io.rr_io.response.valid, 1)
  expect(c.io.hdu_mem.mem_busy, 1,
         """When the Mem stage recieves a valid response from APB it
         should keep signaling that it is busy until the next cycle."""
       )

  step(1)
  poke(c.io.rr_io.response.valid, 0)
  expect(c.io.hdu_mem.mem_busy, 0)

  reset_all()

  println("Mem will hold the address signal during an MMIO request")
  poke  (c.io.exe_mem.wrb_ctrl.rd_wen, 1)
  poke  (c.io.exe_mem.mem_ctrl.read, 1)
  poke  (c.io.exe_mem.alu_result, apb_address)
  expect(c.io.rr_io.request.bits.addr, apb_address)
  step(1)
  poke  (c.io.exe_mem.wrb_ctrl.rd_wen, 0)
  poke  (c.io.exe_mem.mem_ctrl.read, 0)
  poke  (c.io.exe_mem.alu_result, 0)
  expect(c.io.rr_io.request.valid, 1)
  expect(c.io.rr_io.request.bits.addr, apb_address)

  reset_all()

  println(
    """Mem will not drop the second instruction when executing two
    MMIO write instructions back-to-back.

    The first instruction will write a 1 to apb_address, the second
    instruction will write a 0 to apb_address, effectively creating a
    pulse on apb_address."""
  )

  // exe sends the first instruction to mem
  poke  (c.io.exe_mem.mem_ctrl.write, 1)
  poke  (c.io.exe_mem.rs2, 1) // Value to be written
  poke  (c.io.exe_mem.alu_result, apb_address)

  step(1)
  // exe will be stalled by mem
  expect(c.io.hdu_mem.mem_busy, 1)

  // exe will send a bubble to mem
  poke  (c.io.exe_mem.mem_ctrl.write, 0)
  poke  (c.io.exe_mem.rs2, 0) // Value to be written
  poke  (c.io.exe_mem.alu_result, 0)

  step(1)
  // ROAM signals that the MMIO request went through
  poke  (c.io.rr_io.response.valid, 1)

  // mem keeps stalling the pipeline for 1 more cycle
  expect(c.io.hdu_mem.mem_busy, 1)

  step(1)

  // mem stops stalling the pipeline
  expect(c.io.hdu_mem.mem_busy, 0)

  // exe sends the second instruction to mem
  poke  (c.io.exe_mem.mem_ctrl.write, 1)
  poke  (c.io.exe_mem.rs2, 0) // Value to be written
  poke  (c.io.exe_mem.alu_result, apb_address)

  // Mem should now issue the second write
  expect(c.io.rr_io.request.valid, 1)
  expect(c.io.rr_io.request.bits.wdata, 0)
  expect(c.io.rr_io.request.bits.addr, apb_address)
  expect(c.io.rr_io.request.bits.write, 1)

  reset_all()

  println("Mem should mux between pc_next and alu_result")
  // Writeback can write either a memory read (mem_read_data), an alu
  // result (alu_result), or PC + 4 (pc_next) to the register
  // bank. Decode determines which one based on the instrucion and
  // encodes this in rd_sel. Muxing between these options in writeback
  // is expensive and on the critical path, so we do the mux between
  // alu_result and pc_next in Memory instead.

  val RD_SEL_ALU = 0
  poke(c.io.exe_mem.wrb_ctrl.rd_sel, RD_SEL_ALU)
  poke(c.io.exe_mem.alu_result, 4)
  step(1)
  expect(c.io.mem_wrb.pc_next_or_alu_result_or_mult_result, 4, "Mem should choose alu_result when rd_sel is 0")

  reset_all()

  val RD_SEL_PC_NEXT = 2
  poke(c.io.exe_mem.wrb_ctrl.rd_sel, RD_SEL_PC_NEXT)
  poke(c.io.exe_mem.alu_result, 4)
  poke(c.io.exe_mem.pc_next, 8)
  step(1)
  expect(c.io.mem_wrb.pc_next_or_alu_result_or_mult_result, 8, "Mem should choose pc_next when rd_sel is 2")

  step(1)
}
