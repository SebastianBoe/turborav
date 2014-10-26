package TurboRav

import Chisel._
import Common._

class RomTest(c: Rom, conf: TurboravConfig) extends Tester(c) {
  val HI = 1
  val LO = 0

  // Sanity test. start with idling, then read a value from the rom.
  poke(c.io.addr, LO)
  poke(c.io.sel, LO)
  poke(c.io.write, LO)

  expect(c.io.enable, LO)
  expect(c.io.ready, LO)
  expect(c.io.rdata, LO)
  step(1)
}
