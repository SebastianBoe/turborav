package TurboRav

import Chisel._
import Common._
import Constants._

/**
  This module intantiates a ROM and a RAM and presents an interface
  for reads and writes to the modules (although not writes to the ROM
  obviously). All accesses are single-cycle.

  The memory stage has priority over the fetch stage.

  The fetch stage can only read from ROM.
  */
class Roam extends Module {
  val io = new Bundle {
    val fch     = new RequestResponseIo().flip
    val mem     = new RequestResponseIo().flip
    val mmio_rr = new RequestResponseIo()
  }
  // TODO: change the rom and ram modules to support the RR interface
  // instead. That would kill a lot of code here.
  val rom = Module(new Rom())
  val ram = Module(new Ram())

  val memReadingRom =
    io.mem.request.valid && isRomAddress(io.mem.request.bits.addr)

  val memRequestingMmio =
    io.mem.request.valid && isApbAddress(io.mem.request.bits.addr)

  rom.io.pc := Mux(
    memReadingRom,
    io.mem.request.bits.addr,
    io.fch.request.bits.addr
  )

  ram.io.addr   := io.mem.request.bits.addr
  ram.io.word_w := io.mem.request.bits.wdata
  ram.io.wen    := io.mem.request.valid && io.mem.request.bits.write

  // Since the memory stage has priority over the fetch stage and both
  // the ROM and the RAM have single-cycle access the response valid
  // signal becomes equivalent to the request valid signal.
  io.mem.response.valid := io.mem.request.valid
  io.mem.response.bits.word  := MuxCase(ram.io.word_r, Array(
    memReadingRom     -> (rom.io.instr)                  ,
    memRequestingMmio -> (io.mmio_rr.response.bits.word)
  ))

  io.fch.response.bits.word  := rom.io.instr
  io.fch.response.valid := ! memReadingRom && io.fch.request.valid

  io.mmio_rr.request.bits := io.mem.request.bits
  io.mmio_rr.request.valid := memRequestingMmio
}
