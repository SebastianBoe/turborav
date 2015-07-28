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

  // Keeps track of which of the 10 bits we are currently
  // transmitting. NB: This is not related to encoder.io.cnt, which
  // counts the balance of 1's and 0's in the RGB pixels.
  val bit_counter = Counter(cond = io.enable, n = 10)
  val bit_counter_value = bit_counter._1
  val bit_counter_wrap  = bit_counter._2

  // Create three combinatorial encoders and serialize their output
  // with bit_counter.
  for (i <- 0 to num_chan - 1) {
    val encoder = Module(new dvi_tmds_encoder())
    encoder.io.c  := io.ctl(i)
    encoder.io.d  := io.rgb(i)
    encoder.io.de := io.de

    // The encoder computes what the next cnt value should be, and we
    // capture that cnt value at 10-bit character boundaries.
    val encoder_cnt = Reg(init = SInt(0, 4))
    when(bit_counter_wrap) { encoder_cnt := encoder.io.cnt_next }
    encoder.io.cnt := encoder_cnt

    io.dvi_io.chan(i) := encoder.io.q_out(bit_counter_value)
  }
}
