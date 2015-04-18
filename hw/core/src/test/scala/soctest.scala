package TurboRav

import Chisel._
import Constants._

class SocTest(c: Soc) extends Tester(c) {
  step(1000)
  print_regs()

  // Could easily do something similair with the memory when we want
  // to.
  def print_regs() = {
    val regs = Array.ofDim[BigInt](32)

    for(i <- 0 until 32){
      regs(i) = peekAt(c.ravv.dec.regbank.regs, i)
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
