// Copyright (C) 2015 Sebastian Bøe, Joakim Andersson
// License: BSD 2-Clause (see LICENSE for details)

package TurboRav

import Chisel._

import Array._
import java.math.BigInteger;
import scala.sys.process._;

class Rom(elf_path: String) extends Module {
  val io = new Bundle {
    val addr    = UInt(INPUT, Config.xlen)
    val instr = UInt(OUTPUT, Config.xlen)
    val byte_en = UInt(INPUT, 2)
  }

  // Create ROM
  val rom_array = parseRomContents(elf_path)
  val rom = Vec(rom_array)

  val reg_byte_en        = RegNext(io.byte_en) // Does init matter?
  val is_word_access     = reg_byte_en === UInt(0)
  val is_byte_access     = reg_byte_en === UInt(1)
  val is_halfword_access = reg_byte_en === UInt(2)

  val byte_mask = UInt("h000000FF")
  val halfword_mask = UInt("h0000FFFF")

  // The pc addresses individual bytes, but the ROM stores 4-byte
  // words and assumes that all addresses are word-aligned. To go from
  // a byte-addressable address to a word addressable address we
  // right-shift twice.

  val next_word_addr = io.addr >> UInt(2)
  val word_addr = Reg(init = UInt(0), next = next_word_addr)
  val rom_word = rom(word_addr)
  when(is_word_access) {
    io.instr := rom_word
  }.otherwise {
    // Shift and mask the 32 bit word to be able to read only one of
    // the 4 bytes, or one of the two halfwords in the 32bit word.

    val shifted = rom_word >> (RegNext(io.addr(1, 0)) * UInt(8))
    val mask = Mux(is_byte_access, byte_mask, halfword_mask)
    val masked = shifted & mask
    io.instr := masked
  }

  private def parseRomContents(elf_path: String): Array[UInt] = {
    val elf_path_bin = s"$elf_path.bin"
    Seq(
      "/opt/riscv/bin/riscv64-unknown-elf-objcopy",
      "-O",
      "binary",
      elf_path,
      elf_path_bin
    ).!

    val dump = Seq(
      "hexdump",
      "-v",
      "-e",
      "1/4 \"%08X\" \"\\n\"",
      elf_path_bin
    ).lineStream

    Seq("rm", elf_path_bin).! // Delete the temp file.

    dump
      .toArray
      .map(new BigInteger(_, 16))
      .map(UInt(_))
  }
}
