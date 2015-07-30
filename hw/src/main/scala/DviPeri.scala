package TurboRav

import Chisel._

class DviPeri extends Module with ApbSlave {
  val io = new Bundle {
    val apb_slave = new ApbSlaveIo()
    val dvi = new DviIo()
  }

  def getApbSlaveIo = io.apb_slave

  val s_idle :: s_setup :: Nil = Enum(UInt(), 2)
  val state = Reg(init = s_idle)

  // The transmitter needs us to hold it's inputs while it is
  // transmitting. Bah, all this implicit handshaking knowledge is
  // nasty, should be consistent in how values are communicated
  // between modules. What is good digitial design practice?
  val rgb = Reg(init = UInt(0, 8 * 3))
  val ctl = Reg(init = UInt(0, 2 * 3))
  val de  = Reg(init = Bool(false))

  io.apb_slave.out.ready := Bool(false)
  io.apb_slave.out.rdata := Bool(false)

  switch(state) {
    is(s_idle) {
      when(io.apb_slave.sel) {
        state := s_setup
      }
    }
    is(s_setup) {
      state := s_idle
      io.apb_slave.out.ready := Bool(true)
      rgb := io.apb_slave.in.wdata
    }
  }

  val dviTmdsTx = Module(new dvi_tmds_transmitter())
  dviTmdsTx.io.rgb := splitBitsIntoVec(rgb, 3)
  dviTmdsTx.io.ctl := splitBitsIntoVec(ctl, 3)
  dviTmdsTx.io.de  := Bool(true) //TODO: Support START task.

  io.dvi := dviTmdsTx.io.dvi
}
