package TurboRav

import Chisel._
import Constants._

class SocTest(c: Soc) extends Tester(c) {

  step(200)
  expect(c.ravv.exe.io.exe_mem.alu_result, 3)

  print_regs()

  def print_regs() = {
    val regs = Array.ofDim[BigInt](32)

    for(i <- 0 until 32){
      poke(c.ravv.dec.regbank.io.debug_addr,i)
      regs(i) = peek(c.ravv.dec.regbank.io.debug_data)
    }

    print("\n\nRegister bank:\n")
    for(i <- 0 until 32 / 4){
      for(j <- 0 until 4){
        val x= 4*i+j
        print("x%02d: %08x\t".format(x,regs(x)))
      }
      print("\n")
    }
    print("\n\n")
  }

}
