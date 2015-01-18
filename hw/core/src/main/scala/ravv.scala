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

  // TODO: Connect stall signals in a loop instead.
  fch.io.i_stall := mem.io.o_stall
  dec.io.i_stall := mem.io.o_stall
  exe.io.i_stall := mem.io.o_stall
  mem.io.i_stall := Bool(false)
  wrb.io.i_stall := Bool(false)

  val arbiter = Module(new RavVMemoryRequestArbiter())
  arbiter.io.ravv <> io
  arbiter.io.fch  <> fch.io.requestResponseIo
  arbiter.io.mem  <> mem.io.requestResponseIo
}
