package TurboRav

import Chisel._
import Common._
import Constants._
import Apb._

/* The Rav V processor core */
class RavV () extends Module {
  val io = new RavVToTileIo()

  val fch = Module(new Fetch())
  val dec = Module(new Decode())
  val exe = Module(new Execute())
  val mem = Module(new Memory())
  val wrb = Module(new Writeback())

  fch.io.fch_dec <> dec.io.fch_dec
  dec.io.dec_exe <> exe.io.dec_exe
  exe.io.exe_mem <> mem.io.exe_mem
  mem.io.mem_wrb <> wrb.io.mem_wrb
  wrb.io.wrb_dec <> dec.io.wrb_dec
}

class RavVToTileIo () extends Bundle {
  val io = new Bundle() {
    // This is used to make a request for a new instruction.
    val request  = new ValidIO(UInt(width = Config.xlen)).flip()

    // This is used to respond with an instruction
    val response = new ValidIO(UInt(width = Config.xlen))
  }
}

