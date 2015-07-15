package TurboRav

import Chisel._

import Array._
import java.math.BigInteger;
import scala.sys.process._;

class Rom(elf_path: String) extends Module {
  val io = new Bundle {
    val pc    = UInt(INPUT, Config.xlen)
    val instr = UInt(OUTPUT, Config.xlen)
  }

  // The pc addresses individual bytes, but the ROM stores 4-byte
  // words and assumes that all addresses are word-aligned. To go from
  // a byte-addressable address to a word addressable address we
  // right-shift twice.
  val word_addr = io.pc >> UInt(2)

  // Create ROM
  val rom_array = parseRomContents(elf_path)
  val rom = Vec(rom_array)

  // Read from rom
  io.instr := rom(word_addr)

  private def parseRomContents(elf_path: String): Array[UInt] = {
    Seq(
      "riscv64-unknown-elf-objcopy",
      "-O",
      "binary",
      elf_path,
      s"$elf_path.bin"
    ).!
    val dump = Seq(
      "hexdump",
      "-v",
      "-e",
      "1/4 \"%08X\" \"\\n\"",
      s"$elf_path.bin"
    ).lineStream
    Seq("rm", s"$elf_path.bin").! // Delete the temp file.
    return dump
      .toArray
      .map(new BigInteger(_, 16))
      .map(UInt(_))
  }
}