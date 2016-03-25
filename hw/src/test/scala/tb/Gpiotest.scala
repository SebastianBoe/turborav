package TurboRav

import Chisel._

class GpioTest(c: Gpio) extends JUnitTester(c) {
  def apb_access(addr: Int, write: Boolean, wdata: Int) = {
    poke(c.io.apb_slave.sel, 1)
    poke(c.io.apb_slave.in.write, if (write) 1 else 0)
    poke(c.io.apb_slave.in.addr, addr)
    poke(c.io.apb_slave.in.wdata, wdata)
    step(1)
  }

  def apb_no_access() = {
    poke(c.io.apb_slave.sel, 0)
    poke(c.io.apb_slave.in.write, 0)
    poke(c.io.apb_slave.in.addr, 0)
    poke(c.io.apb_slave.in.wdata, 0)
    step(4)
  }

  val NUM_GPIO_PINS = 64
  val INPUT_PINS_ADDRESS_SPACE_SIZE = 64 * 4

  poke(c.io.pin_inputs, 2)
  step(1)
  apb_access(0, false, 0)
  expect(c.io.apb_slave.out.rdata, 0)
  expect(c.io.apb_slave.out.ready, 1)
  step(1)
  apb_no_access()

  apb_access(4, false, 0)
  expect(c.io.apb_slave.out.rdata, 1)
  expect(c.io.apb_slave.out.ready, 1)
  step(1)
  apb_no_access()

  apb_access(INPUT_PINS_ADDRESS_SPACE_SIZE + 4, true, 1)
  expect(c.io.apb_slave.out.ready, 1)
  step(1)
  expect(c.io.pin_outputs, 2)
  apb_no_access()
}
