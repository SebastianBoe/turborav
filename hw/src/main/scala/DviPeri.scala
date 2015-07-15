package TurboRav

import Chisel._

class DviPeri extends Module {
  val io = new Bundle {
    val dvi_io  = new DviIo()
  }
  val dviTmdsTx = Module(new dvi_tmds_transmitter())
  dviTmdsTx.io.rgb := Vec(UInt(0), UInt(20), UInt(50))
  dviTmdsTx.io.ctl := Vec(UInt(0), UInt(0 ), UInt(0 ))
  dviTmdsTx.io.de  := Bool(true)
  io.dvi_io := dviTmdsTx.io.dvi_io
}
