package TurboRav

import Chisel._
import Common._

class RomTest(c: Rom) extends Tester(c) {
  val HI = 1
  val LO = 0

  // Convienience function
  def expect_(
    enable : BigInt,
    ready  : BigInt,
    rdata  : BigInt) = {
    expect(c.io.enable, enable)
    expect(c.io.ready, ready)
    expect(c.io.rdata, rdata)
  }

  // Sanity test. start with idling, then read a value from the rom.
  poke(c.io.addr, LO)
  poke(c.io.sel, LO)
  poke(c.io.write, LO)

  expect(c.io.enable, LO)
  expect(c.io.ready, LO)
  expect(c.io.rdata, LO)
  step(1)
  // Enter setup state. (See APB spec.)
  poke(c.io.sel, HI)
  expect_(
    enable = LO,
    ready  = LO,
    rdata  = LO
  )
  step(2)
  expect_(
    enable = HI,
    ready  = 1,
    rdata  = 1
  )
  poke(c.io.sel, LO)
  step(1)
  expect_(
    enable = 0,
    ready  = 0,
    rdata  = 0
  )

}
