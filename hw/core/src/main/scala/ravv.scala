package TurboRav

import Chisel._
import Common._
import Constants._
import Apb._

/* The Rav V processor core */
class RavV extends Module {
  val io = new RequestResponseIo()

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

  // TODO: Connect stall signals using loops instead.
  val stall = orR(Cat(
    // There should be more stages that generate stall signals soon.
    mem.io.o_stall
  ))
  fch.io.i_stall := stall
  dec.io.i_stall := stall
  exe.io.i_stall := stall
  mem.io.i_stall := stall
  wrb.io.i_stall := stall

  val arbiter = Module(new RavVMemoryRequestArbiter())
  arbiter.io.ravv <> io
  arbiter.io.fch  <> fch.io.requestResponseIo
  arbiter.io.mem  <> mem.io.requestResponseIo
}

class RequestResponseIo extends Bundle {
  val request  = new ValidIO( new Bundle {
    val addr  = UInt(width = Config.xlen)
    val wdata = UInt(width = Config.xlen)
    val write = Bool()
  })

  val response = new ValidIO(new Bundle {
    val word = UInt(width = Config.xlen)
  }).flip()
}
