package TurboRav

import Chisel._

class RegBankTest(c: RegBank) extends JUnitTester(c) {
  val xlen = Config.xlen
  private def read1(rx: Int, data: BigInt) = {
    poke(c.io.reads.rs1.bits, rx)
    step(1)
    expect(c.io.rs1_data, data)
  }

  private def read2(rx: Int, data: BigInt) = {
    poke(c.io.reads.rs2.bits, rx)
    step(1)
    expect(c.io.rs2_data, data)
  }

  private def write(rx: Int, data: BigInt)  = {
    poke(c.io.write.valid,  1)
    poke(c.io.write.bits.addr, rx)
    poke(c.io.write.bits.data, data)
    step(1)
    poke(c.io.write.valid,  0)
  }

  val one = BigInt(1)
  val max = (one << xlen) - one
  val x0 = 0
  val x1 = 1
  val x31 = 31

  write(x0, max)
  read1(x0, 0)
  read2(x0, 0)

  write(x1, one)
  read1(x1, one)
  read2(x1, one)

  write(x31, max)
  read1(x31, max)
  read2(x1,  one)

  for(i <- 1 until 32){
    write(i,    i*i)
    read1(i,     i*i)
    read2((i-1), (i-1)*(i-1))
  }

}
