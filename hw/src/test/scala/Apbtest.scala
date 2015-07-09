package TurboRav

import Chisel._
// import Constants._
// import Apb._

import Array._
import java.math.BigInteger;

class ApbControllerTest(c: ApbController) extends Tester(c) {

  def poke_rr(addr: Int, data: Int, write: Boolean) = {
    poke(c.io.rr.request.bits.addr, addr)
    poke(c.io.rr.request.bits.wdata, data)
    poke(c.io.rr.request.valid, 1)
    poke(c.io.rr.request.bits.write, if (write) 1 else 0)
  }

  def poke_apb(data: Int, ready: Boolean) = {
    poke(c.io.apb_rdata, data)
    poke(c.io.apb_ready, if (ready) 1 else 0)
  }

  def expect_apb(addr: Int, data: Int, write: Boolean, enable: Boolean, selx: Int) = {
    expect(c.io.apb_wdata, data)
    expect(c.io.apb_addr, addr)
    expect(c.io.apb_write,  if (write ) 1 else 0)
    expect(c.io.apb_enable, if (enable) 1 else 0)
    expect(c.io.apb_selx, selx)
  }

  def expect_rr(data: Int, valid: Boolean) = {
    expect(c.io.rr.response.valid, if(valid) 1 else 0)
    expect(c.io.rr.response.bits.word, data)
    expect(c.io.rr.response.bits.has_wait_state, 0)
  }

  def clear_rr() = {
    poke(c.io.rr.request.bits.addr, 0)
    poke(c.io.rr.request.bits.wdata, 0)
    poke(c.io.rr.request.valid, 0)
    poke(c.io.rr.request.bits.write, 0)
  }

  def clear_apb() = {
    poke(c.io.apb_rdata, 0)
    poke(c.io.apb_ready, 0)
  }

  // Test start in idle state
  step(1)
  expect_apb(0, 0, false, false, 0)

  // Test single write transaction with no wait state
  poke_rr(0x20040040, 0xdeadbeef, true)
  step(1)
  expect_apb(0x40, 0xdeadbeef, true, false, 1 << 4)
  expect_rr(0, false)

  clear_rr()
  step(1)
  poke_apb(0xbaadf00d, true)
  expect_apb(0x40, 0xdeadbeef, true, true, 1 << 4)
  expect_rr(0x0, true)

  step(1)
  clear_apb()
  expect_apb(0, 0, false, false, 0)

  step(1)
  expect_apb(0, 0, false, false, 0)

  // Test single read transaction with no wait state
  poke_rr(0x20020080, 0xbaadf00d, false)
  step(1)
  expect_apb(0x80, 0x0, false, false, 1 << 2)
  expect_rr(0, false)

  clear_rr()
  step(1)
  poke_apb(0xdeadbeef, true)
  expect_apb(0x80, 0, false, true, 1 << 2)
  expect_rr(0xdeadbeef, true)

  step(1)
  clear_apb()
  expect_apb(0, 0, false, false, 0)

  step(1)
  expect_apb(0, 0, false, false, 0)

    // Test single write transaction with no wait state
  poke_rr(0x20040040, 0xdeadbeef, true)
  step(1)
  expect_apb(0x40, 0xdeadbeef, true, false, 1 << 4)
  expect_rr(0, false)

  clear_rr()
  step(1)
  poke_apb(0xbaadf00d, false)
  for(i <- 0 until 4){
    expect_apb(0x40, 0xdeadbeef, true, true, 1 << 4)
    expect_rr(0x0, false)
    step(1)
  }
  poke_apb(0xbaadf00d, true)
  expect_apb(0x40, 0xdeadbeef, true, true, 1 << 4)
  expect_rr(0x0, true)

  step(1)
  clear_apb()
  expect_apb(0, 0, false, false, 0)

  step(1)
  expect_apb(0, 0, false, false, 0)

  // Test single read transaction with wait state
  poke_rr(0x20020080, 0xbaadf00d, false)
  step(1)
  expect_apb(0x80, 0x0, false, false, 1 << 2)
  expect_rr(0, false)

  clear_rr()
  step(1)
  poke_apb(0xdeadbeef, false)
  for(i <-0 until 4){
    expect_apb(0x80, 0, false, true, 1 << 2)
    expect_rr(0x0, false)
    step(1)
  }
  poke_apb(0xdeadbeef, true)
  expect_apb(0x80, 0, false, true, 1 << 2)
  expect_rr(0xdeadbeef, true)

  step(1)
  clear_apb()
  expect_apb(0, 0, false, false, 0)

  step(1)
  expect_apb(0, 0, false, false, 0)

  // Test back to back write read transaction
  poke_rr(0x20040040, 0xdeadbeef, true)
  step(1)
  expect_apb(0x40, 0xdeadbeef, true, false, 1 << 4)
  expect_rr(0, false)

  clear_rr()
  step(1)
  poke_apb(0xbaadf00d, true)
  expect_apb(0x40, 0xdeadbeef, true, true, 1 << 4)
  expect_rr(0, true)

  poke_rr(0x20040080, 0xdeadbeef, false)
  step(1)
  expect_apb(0x80, 0, false, false, 1 << 4)
  expect_rr(0, false)

  clear_rr()
  step(1)
  poke_apb(0xbaadf00d, true)
  expect_apb(0x80, 0, false, true, 1 << 4)
  expect_rr(0xbaadf00d, true)

  step(1)
  clear_apb()
  expect_apb(0, 0, false, false, 0)

  step(1)
  expect_apb(0, 0, false, false, 0)

}
