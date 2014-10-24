package TurboRav

import Chisel._

class RegBankTest(c: RegBank) extends Tester(c) {

  def read1(rx: Int, data: BigInt) = {
    poke(c.io.rs1_addr, rx)
    expect(c.io.rs1_data, data)
  }

  def read2(rx: Int, data: BigInt) = {
    poke(c.io.rs2_addr, rx)
    expect(c.io.rs2_data, data)
  }

  def write(rx: Int, data: BigInt)  = {
    poke(c.io.rd_wen,  1)
    poke(c.io.rd_addr, rx)
    poke(c.io.rd_data,  data)
    step(1)
    //poke(c.io.rd_wen,  false)
  }

  val one = BigInt(1)
  val max = (one << c.xlen) - one
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
