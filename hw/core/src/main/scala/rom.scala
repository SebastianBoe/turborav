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

  // Hardcoding this until i figure out how to read contents from
  // file.
  val rom = Vec(range(0, 32).map(UInt(_)))

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
