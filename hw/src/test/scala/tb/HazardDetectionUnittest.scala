package TurboRav

import Chisel._

class HazardDetectionUnitTest(c: HazardDetectionUnit) extends JUnitTester(c) {
  println("HDU should combinatorially trigger stall signals.")
  poke(c.io.hdu_exe.mult_busy, 1)
  expect(c.io.hdu_fch.stall, 1)
}
