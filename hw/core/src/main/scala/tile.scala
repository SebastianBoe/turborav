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


}
