package TurboRav

import Chisel._

class FetchTest(c: Fetch) extends JUnitTester(c) {
  val INSTRUCTION = 2
  val RO_DATA = 3

  def reset_all() = {
    println("Reset in between tests")
    step(1)
    clear_all_pokes()
    reset(1)
    step(1)
    println("End of reset")
  }

  def clear_all_pokes() = {
    // NB: Must add all the poke values used in the tb here.
    // This sucks, luckily, Chisel3's unit testing is much better.
    poke(c.io.rr_io.response.valid, 0)
    poke(c.io.rr_io.response.bits.word, 0)
  }

  println("Normal usage. Fetch reads an instruction from the ROM and gives it to Decode.")
  poke(c.io.rr_io.response.valid, 0)
  expect(c.io.rr_io.request.valid, 1)
  expect(c.io.fch_dec.instr_valid, 0)

  step(1)
  poke(c.io.rr_io.response.valid, 1)
  poke(c.io.rr_io.response.bits.word, INSTRUCTION)

  expect(c.io.fch_dec.instr_valid, 1)
  expect(c.io.fch_dec.instr, INSTRUCTION)

  reset_all()

  // Simulate a structural hazard between fetch and the memory
  // stage. They both want to read the ROM, but the ROM only has 1
  // read-port. So fetch needs to stall until the ROM is available.

  // Fetch discovers this structural hazard by observing that the
  // RequestResponse interface to Roam is not returning a valid
  // response. It reacts by not updating PC, and sending a bubble down
  // the pipeline.

  poke(c.io.rr_io.response.valid, 0)
  poke(c.io.rr_io.response.bits.word, RO_DATA)
  expect(c.io.rr_io.request.bits.addr, 0)
  step(1)
  expect(c.io.fch_dec.instr_valid, 0)
  expect(c.io.fch_dec.instr, 0, "\nFetch should send a bubble to decode.\n")
  expect(c.io.rr_io.request.bits.addr, 0)
  step(1)
  // Finally the ROM becomes available
  poke(c.io.rr_io.response.valid, 1)
  poke(c.io.rr_io.response.bits.word, INSTRUCTION)

  expect(c.io.rr_io.request.bits.addr, 4)
  expect(c.io.fch_dec.instr_valid, 1)
  expect(c.io.fch_dec.instr, INSTRUCTION)
  expect(c.io.fch_dec.pc, 0)
  step(1)
  expect(c.io.fch_dec.pc, 4)
  expect(c.io.rr_io.request.bits.addr, 8)
}
