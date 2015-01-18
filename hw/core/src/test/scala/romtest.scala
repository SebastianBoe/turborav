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

  def verify_transaction(address: BigInt, expected_value: BigInt) = {
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

  val rom_array = c.rom_array

  for (i <- 0 to rom_array.size - 1) {
    verify_transaction(
      address = i * 4,
      expected_value = rom_array(i)
    )
  }
}
