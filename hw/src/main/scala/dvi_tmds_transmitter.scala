package TurboRav

import Chisel._
import Constants._

// This class implements the transmitter seen in figure 3-2 of the DVI
// V1.0 spec.
class dvi_tmds_transmitter extends Module {
  val io = new Bundle(){
    val rgb   = UInt(INPUT, 24)
    val ctl   = UInt(INPUT, 6) // 2 lower bits are HSYNC and VSYNC
    val de    = Bool(INPUT)
  }
  for (i <- 1 to 3) {
    val encoder = Module(new dvi_tmds_encoder())
    encoder.io.c := io.ctl(i * 2 - 1, i * 2 - 2)
    encoder.io.d := io.rgb(i * 8 - 1, i * 8 - 8)
  }
}
