package TurboRav

import Chisel._
import Constants._

// This class implements the transmitter seen in figure 3-2 of the DVI
// V1.0 spec.
class dvi_tmds_transmitter extends Module {
  val num_chan = 3
  val pixel_bits = 8
  val io = new Bundle(){
    val rgb   = Vec.fill(num_chan) { UInt(width = pixel_bits) }.asInput()
    val ctl   = Vec.fill(num_chan) { UInt(width = 2) }.asInput()
    val de    = Bool(INPUT)

    val chan  = Vec.fill(num_chan) { UInt(width = 1) }.asOutput()
  }
  for (i <- 1 to num_chan) {
    val encoder = Module(new dvi_tmds_encoder())
    encoder.io.c  := io.ctl(i)
    encoder.io.d  := io.rgb(i)
    encoder.io.de := io.de
  }
}
