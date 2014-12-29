package TurboRav

import Chisel._
import Common._

class RomTest(c: Rom) extends Tester(c) {
  // Convienience function
  def expect_(
    enable : BigInt,
    ready  : BigInt,
    rdata  : BigInt) = {
    expect(c.io.enable, enable)
    expect(c.io.ready, ready)
    expect(c.io.rdata, rdata)
  }

  def verify_transaction(address: Int, expected_value: Int) = {
    // Sanity test. start with idling, then read a value from the rom.
    poke(c.io.sel, 0)
    poke(c.io.write, 0)

    expect(c.io.enable, 0)
    expect(c.io.ready, 0)
    expect(c.io.rdata, 0)

    step(1)
    // Enter setup state. (See APB spec.)
    poke(c.io.sel, 1)
    poke(c.io.addr, address)
    expect_(
      enable = 0,
      ready  = 0,
      rdata  = 0
    )
    step(1)
    expect_(
      enable = 1,
      ready  = 1,
      rdata  = expected_value
    )
    step(1)
    poke(c.io.sel, 0)
    poke(c.io.addr, 0)
    expect_(
      enable = 0,
      ready  = 0,
      rdata  = 0
    )
    step(1)
    step(1)
  }

  // This is copy-pasted from rom.scala, but this is ok because it is
  // only temporary.
  val rom_array = Array(
    0x00100793, // li	a5,1
    0x00200713, // li	a4,2
    0x00f706b3, // add	a3,a4,a5
    0x00000063 // b	c <main+0xc> //Should jump to 0
  )

  for (i <- 0 to rom_array.size - 1) {
    verify_transaction(
      address = i,
      expected_value = rom_array(i)
    )
  }
}
