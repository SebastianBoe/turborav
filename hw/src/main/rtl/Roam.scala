// Copyright (C) 2015 Sebastian Bøe, Joakim Andersson
// License: BSD 2-Clause (see LICENSE for details)

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
class Roam(elf_path: String, fpga: Boolean) extends Module {
  val io = new Bundle {
    val fch     = new RequestResponseIo().flip
    val mem     = new Bundle {
      val rr = new RequestResponseIo().flip
    }
    val rr_mmio = new RequestResponseIo()
  }
  // TODO: change the rom and ram modules to support the RR interface
  // instead. That would kill a lot of code here.
  val rom = Module(new Rom(elf_path))
  val ram = Module(if (fpga) new FpgaRam() else new Ram())

  // Alias for the memory request and response
  val request  = io.mem.rr.request
  val response = io.mem.rr.response

  val mem_reading_rom     = request.valid && isRomAddress(request.bits.addr)
  val mem_reading_ram     = request.valid && isRamAddress(request.bits.addr)
  val mem_requesting_mmio = request.valid && isApbAddress(request.bits.addr)
  assert(
    ! request.valid ||
    request.valid && request.bits.addr(31, 30) === UInt(0),
    "isApbAddress assumes the top two bits are unused"
  )

  rom.io.addr := Mux(
    mem_reading_rom,
    request.bits.addr,
    io.fch.request.bits.addr
  )

  rom.io.byte_en := Mux(
    mem_reading_rom,
    request.bits.byte_en,
    UInt(0)
  )

  ram.io.addr    := request.bits.addr
  ram.io.word_w  := request.bits.wdata
  ram.io.byte_en := request.bits.byte_en
  ram.io.wen     := request.valid &&  request.bits.write
  ram.io.ren     := request.valid && !request.bits.write

  // Since the memory stage has priority over the fetch stage and both
  // the ROM and the RAM have single-cycle access the response valid
  // signal becomes equivalent to the request valid signal.
  response.valid := Mux(
    mem_requesting_mmio,
    io.rr_mmio.response.valid,
    request.valid
  )
  response.bits.word := MuxCase(ram.io.word_r, Array(
    RegNext(mem_reading_rom) -> (rom.io.instr),
    mem_requesting_mmio -> (io.rr_mmio.response.bits.word)
  ))

  io.fch.response.bits.word  := rom.io.instr
  io.fch.response.valid      := ! RegNext(mem_reading_rom)

  io.rr_mmio.request.bits  := request.bits
  io.rr_mmio.request.valid := mem_requesting_mmio
}
