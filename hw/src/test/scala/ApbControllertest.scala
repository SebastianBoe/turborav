package TurboRav

import Chisel._

class ApbControllerTest(c: ApbController) extends Tester(c) {

  def pokeRr(addr: Int, data: Int, write: Boolean) {
    poke(c.io.rr.request.bits.addr, addr)
    poke(c.io.rr.request.bits.wdata, data)
    poke(c.io.rr.request.valid, 1)
    poke(c.io.rr.request.bits.write, if (write) 1 else 0)
  }

  def pokeApb(data: Int, ready: Boolean) {
    poke(c.io.out.rdata, data)
    poke(c.io.out.ready, if (ready) 1 else 0)
  }

  def expectApb(addr: Int, data: Int, write: Boolean, enable: Boolean, selx: Int) {
    expect(c.io.in.wdata, data)
    expect(c.io.in.addr, addr)
    expect(c.io.in.write,  if (write ) 1 else 0)
    expect(c.io.in.enable, if (enable) 1 else 0)
    expect(c.io.selx, selx)
  }

  def expectRr(data: Int, valid: Boolean) {
    expect(c.io.rr.response.valid, if(valid) 1 else 0)
    expect(c.io.rr.response.bits.word, data)
  }

  def clearRr() {
    poke(c.io.rr.request.bits.addr, 0)
    poke(c.io.rr.request.bits.wdata, 0)
    poke(c.io.rr.request.valid, 0)
    poke(c.io.rr.request.bits.write, 0)
  }

  def clearApb() {
    poke(c.io.out.rdata, 0)
    poke(c.io.out.ready, 0)
  }

  // Test start in idle state
  step(1)
  expectApb(0, 0, false, false, 0)

  // Test single write transaction with no wait state
  pokeRr(0x20040040, 0xdeadbeef, true)
  step(1)
  expectApb(0x40, 0xdeadbeef, true, false, 1 << 4)
  expectRr(0, false)

  clearRr()
  step(1)
  pokeApb(0xbaadf00d, true)
  expectApb(0x40, 0xdeadbeef, true, true, 1 << 4)
  expectRr(0x0, true)

  step(1)
  clearApb()
  expectApb(0, 0, false, false, 0)

  step(1)
  expectApb(0, 0, false, false, 0)

  // Test single read transaction with no wait state
  pokeRr(0x20020080, 0xbaadf00d, false)
  step(1)
  expectApb(0x80, 0x0, false, false, 1 << 2)
  expectRr(0, false)

  clearRr()
  step(1)
  pokeApb(0xdeadbeef, true)
  expectApb(0x80, 0, false, true, 1 << 2)
  expectRr(0xdeadbeef, true)

  step(1)
  clearApb()
  expectApb(0, 0, false, false, 0)

  step(1)
  expectApb(0, 0, false, false, 0)

    // Test single write transaction with no wait state
  pokeRr(0x20040040, 0xdeadbeef, true)
  step(1)
  expectApb(0x40, 0xdeadbeef, true, false, 1 << 4)
  expectRr(0, false)

  clearRr()
  step(1)
  pokeApb(0xbaadf00d, false)
  for(i <- 0 until 4){
    expectApb(0x40, 0xdeadbeef, true, true, 1 << 4)
    expectRr(0x0, false)
    step(1)
  }
  pokeApb(0xbaadf00d, true)
  expectApb(0x40, 0xdeadbeef, true, true, 1 << 4)
  expectRr(0x0, true)

  step(1)
  clearApb()
  expectApb(0, 0, false, false, 0)

  step(1)
  expectApb(0, 0, false, false, 0)

  // Test single read transaction with wait state
  pokeRr(0x20020080, 0xbaadf00d, false)
  step(1)
  expectApb(0x80, 0x0, false, false, 1 << 2)
  expectRr(0, false)

  clearRr()
  step(1)
  pokeApb(0xdeadbeef, false)
  for(i <-0 until 4){
    expectApb(0x80, 0, false, true, 1 << 2)
    expectRr(0x0, false)
    step(1)
  }
  pokeApb(0xdeadbeef, true)
  expectApb(0x80, 0, false, true, 1 << 2)
  expectRr(0xdeadbeef, true)

  step(1)
  clearApb()
  expectApb(0, 0, false, false, 0)

  step(1)
  expectApb(0, 0, false, false, 0)

  // Test back to back write read transaction
  pokeRr(0x20040040, 0xdeadbeef, true)
  step(1)
  expectApb(0x40, 0xdeadbeef, true, false, 1 << 4)
  expectRr(0, false)

  clearRr()
  step(1)
  pokeApb(0xbaadf00d, true)
  expectApb(0x40, 0xdeadbeef, true, true, 1 << 4)
  expectRr(0, true)

  pokeRr(0x20040080, 0xdeadbeef, false)
  step(1)
  expectApb(0x80, 0, false, false, 1 << 4)
  expectRr(0, false)

  clearRr()
  step(1)
  pokeApb(0xbaadf00d, true)
  expectApb(0x80, 0, false, true, 1 << 4)
  expectRr(0xbaadf00d, true)

  step(1)
  clearApb()
  expectApb(0, 0, false, false, 0)

  step(1)
  expectApb(0, 0, false, false, 0)
}
