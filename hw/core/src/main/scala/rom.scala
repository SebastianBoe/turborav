package TurboRav

import Chisel._

import Common._
import Array._
import Apb._

class Rom() extends Module {
  val io = new SlaveToApbIo()

  // This seems to be a common pattern, but I need a better name for
  // it I think. Surely a name for something this generic should
  // already exist.
  def clearIfDisabled(data: UInt, enabled: Bool):UInt = {
    data & Fill(enabled, data.getWidth())
  }

  // Hardcoding until I am able to read from file correctly. The
  // machine-code hex translation is from core/riscv_test_code
  val rom_array = Array(
    0x00100793, // li	a5,1
    0x00200713, // li	a4,2
    0x00f706b3, // add	a3,a4,a5
    0x00000063 // b	c <main+0xc> //Should jump to 0
  )

  val rom = Vec(rom_array.map(UInt(_)))

  io.rdata  := clearIfDisabled(
    data = rom(Reg(next = io.addr)),
    enabled = io.enable
  )

  val s_idle :: s_ready :: Nil = Enum(UInt(), 2)
  val state = Reg(init = s_idle)

  when( state === s_ready ){
    state := s_idle
  } .elsewhen ( io.sel ) {
    state := s_ready
  } .otherwise {
    state := s_idle
  }

  io.ready  := state === s_ready
  io.enable := io.ready
}
