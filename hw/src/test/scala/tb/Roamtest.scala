package TurboRav

import Chisel._

class RoamTest(c: Roam) extends JUnitTester(c) {
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
    poke  (c.io.mem.rr.request.valid, 0)
    poke  (c.io.mem.rr.request.bits.addr, 0)
    poke  (c.io.mem.rr.request.bits.write, 0)
    poke  (c.io.rr_mmio.response.valid, 0)
    poke  (c.io.rr_mmio.response.bits.word, 0)
  }

  println("MMIO requests are done comb.")
  poke  (c.io.mem.rr.request.valid, 1)
  poke  (c.io.mem.rr.request.bits.addr, apb_address)
  expect(c.io.rr_mmio.request.valid, 1)
  expect(c.io.rr_mmio.request.bits.addr, apb_address)

  reset_all()

  println("MMIO responses are returned comb. to mem when recieved from rr_mmio")
  poke  (c.io.mem.rr.request.valid, 1)
  poke  (c.io.mem.rr.request.bits.addr, apb_address)
  expect(c.io.mem.rr.response.valid, 0)
  step(1)
  poke  (c.io.mem.rr.request.valid, 1)
  expect(c.io.mem.rr.response.valid, 0)
  step(1)
  poke  (c.io.rr_mmio.response.valid, 1)
  poke  (c.io.rr_mmio.response.bits.word, 42)
  expect(c.io.mem.rr.response.valid, 1)
  expect(c.io.mem.rr.response.bits.word, 42)

  reset_all()
}
