package TurboRav

import Chisel._
import Constants._

// This class implements the transmitter seen in figure 3-2 of the DVI
// V1.0 spec.
class dvi_tmds_transmitter extends Module {
  val num_chan = 3
  val pixel_bits = 8
  val io = new Bundle(){
    val enable = Bool(INPUT)
    val rgb    = Vec.fill(num_chan) { UInt(width = pixel_bits) }.asInput()
    val ctl    = Vec.fill(num_chan) { UInt(width = 2) }.asInput()
    val de     = Bool(INPUT)

    val dvi_io  = new DviIo()
  }

  // Create three combinatorial encoders and serialize their output
  // with a sequential serializer.
  for (i <- 0 to num_chan - 1) {
    val encoder = Module(new dvi_tmds_encoder())
    encoder.io.c  := io.ctl(i)
    encoder.io.d  := io.rgb(i)
    encoder.io.de := io.de
    encoder.io.cnt := UInt(0) //TODO
    val cnt_next = encoder.io.cnt_next //TODO

    val s = Module(new Serializer(encoder.io.q_out.getWidth))
    s.io.in := encoder.io.q_out

    // Simply wiring it like this introduces the requirement that the
    // enable signal must be held high until the transmission is over.
    s.io.cond := io.enable

    io.dvi_io.chan(i) := s.io.out
  }
}
