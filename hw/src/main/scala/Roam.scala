package TurboRav

import Chisel._
import Constants._

/**
  This module intantiates a ROM and a RAM and presents an interface
  for reads and writes to the modules (although not writes to the ROM
  obviously). All accesses are single-cycle.

  The memory stage has priority over the fetch stage.

  The fetch stage can only read from ROM.
  */
class Roam(elf_path: String) extends Module {
  val io = new Bundle {
    val fch     = new RequestResponseIo().flip
    val mem     = new Bundle {
      val rr = new RequestResponseIo().flip
      val has_wait_state = Bool(OUTPUT)
    }
    val rr_mmio = new RequestResponseIo()
  }
  // TODO: change the rom and ram modules to support the RR interface
  // instead. That would kill a lot of code here.
  val rom = Module(new Rom(elf_path))
  val ram = Module(new Ram())

  val mem_reading_rom =
    io.mem.rr.request.valid && isRomAddress(io.mem.rr.request.bits.addr)

  val mem_reading_ram =
    io.mem.rr.request.valid && isRamAddress(io.mem.rr.request.bits.addr)

  val mem_requesting_mmio =
    io.mem.rr.request.valid && isApbAddress(io.mem.rr.request.bits.addr)

  rom.io.pc := Mux(
    mem_reading_rom,
    io.mem.rr.request.bits.addr,
    io.fch.request.bits.addr
  )

  ram.io.addr    := io.mem.rr.request.bits.addr
  ram.io.word_w  := io.mem.rr.request.bits.wdata
  ram.io.byte_en := io.mem.rr.request.bits.byte_en
  ram.io.wen     := io.mem.rr.request.valid &&  io.mem.rr.request.bits.write
  ram.io.ren     := io.mem.rr.request.valid && !io.mem.rr.request.bits.write

  // Since the memory stage has priority over the fetch stage and both
  // the ROM and the RAM have single-cycle access the response valid
  // signal becomes equivalent to the request valid signal.
  io.mem.rr.response.valid     := Mux(mem_requesting_mmio,
                                   io.rr_mmio.response.valid,
                                   io.mem.rr.request.valid)
  io.mem.rr.response.bits.word := MuxCase(ram.io.word_r, Array(
    mem_reading_rom     -> (rom.io.instr),
    mem_requesting_mmio -> (io.rr_mmio.response.bits.word)
  ))
  io.mem.has_wait_state := mem_reading_ram

  io.fch.response.bits.word  := rom.io.instr
  io.fch.response.valid      := ! mem_reading_rom && io.fch.request.valid

  io.rr_mmio.request.bits  := io.mem.rr.request.bits
  io.rr_mmio.request.valid := mem_requesting_mmio
}
