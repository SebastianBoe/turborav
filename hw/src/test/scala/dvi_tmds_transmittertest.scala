package TurboRav

import Chisel._

class dvi_tmds_transmitterTest(c: dvi_tmds_transmitter) extends JUnitTester(c) {
  step(1)

  val scale_str = "10101010"
  val scale = ScalaUtil.b(scale_str)

  val expected_output_str = "1000110011" // Computed by hand in the
                                         // encoder test.

  // Data is clocked out LSbit first.
  val expected_output_str_lsb_first = expected_output_str.reverse

  poke(c.io.enable, 1)
  poke(c.io.rgb(0), scale)
  poke(c.io.de, 1)

  for (bin <- expected_output_str_lsb_first) {
    expect(c.io.dvi.chan(0), bin.toString.toInt)
    step(1)
  }
}
