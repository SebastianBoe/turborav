package TurboRav

import Chisel._

class DviIo extends Bundle {
  val chan = Vec.fill(3) {Bool()}.asOutput()

  // From the DVI spec.
  /** The T.M.D.S. clock channel carries a character-rate frequency
   reference from which the receiver produces a bit-rate sample clock for
   the incoming serial streams.
   */
  val chan_clk = Bool(OUTPUT)
}
