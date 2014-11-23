package TurboRav

import Chisel._
import Common._
import Constants._
import Apb._

// To have the memory interface for RavV be as simple as possible we
// create a Tile that presents a simple memory-interface to RavV and
// talks APB outwards.

// The tile instantiates RavV and connects to it a simple,
// request-response interface for memory and converts this interface
// into APB master-compatible signals.
class Tile () extends Module {
  val io = new Bundle (){
    val tile_rav = new RavVToTileIo().flip()
    val tile_apb = new SlaveToApbIo().flip()
  }

  val s_idle :: s_apb_access_phase :: s_apb_transfer_phase :: Nil = Enum(UInt(), 3)
  val state = Reg(init = s_idle)

  when      (state === s_idle) {
    io.tile_rav.response.valid := Bool(false)
    io.tile_rav.response.bits.instruction  := UInt(0, width = Config.xlen)

    when(io.tile_rav.request.valid){
      // We transition from idling to making a request on the apb bus.
      state            := s_apb_access_phase
      io.tile_apb.sel  := Bool(true)
      io.tile_apb.addr := io.tile_rav.request.bits.pc
    }
  }.elsewhen(state === s_apb_access_phase) {
    state := s_apb_transfer_phase
    io.tile_rav.response.valid := Bool(true)
    io.tile_rav.response.bits.instruction  := io.tile_apb.rdata
  }.otherwise {
    state := s_idle
    io.tile_rav.response.valid := Bool(false)
    io.tile_rav.response.bits.instruction  := UInt(0, width = Config.xlen)
    // Is digital design hard or do I just not know how to do it?
    io.tile_apb.addr := UInt(0, width = Config.xlen)
    io.tile_apb.sel := Bool(false)
  }
  io.tile_apb.write := Bool(false)
}
