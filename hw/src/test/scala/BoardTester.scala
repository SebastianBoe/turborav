package TurboRav

import Chisel._
import scala.math.BigInt

/**
 * This tester adds a few utility functions for manipulating pin
 *  signals as they are named on the PCB.
 *
 * NB: I think one should favour composition over inheritance here,
 *  but I was not able to figure out how to do this with
 *  composition. Perhaps with Chisel3 it will be easier.
 * */
class BoardTester(c: Soc, isTrace: Boolean)
extends JUnitTester(c, isTrace) {
  def driveBtn2(level: Boolean) = {
    poke(c.io.pin_inputs, level)
  }

 def getLed1Status(): Boolean = {
    (peek(c.io.pin_outputs) & 2) > 0
  }
}

