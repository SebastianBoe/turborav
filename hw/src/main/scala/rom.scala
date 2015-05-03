package TurboRav

import Chisel._

import Common._
import Array._
import java.math.BigInteger;

class Rom extends Module {
  val io = new Bundle {
    val pc    = UInt(INPUT, Config.xlen)
    val instr = UInt(OUTPUT, Config.xlen)
  }

  // The pc addresses individual bytes, but the ROM stores 4-byte
  // words and assumes that all addresses are word-aligned. To go from
  // a byte-addressable address to a word addressable address we
  // right-shift twice.
  val word_addr = io.pc >> UInt(2)
  assert(io.pc(1,0) === UInt(0), "We assume word-aligned addresses.")

  // Create ROM
  val rom_array = parseRomContents()
  val rom = Vec(rom_array.map(UInt(_)))

  // Read from rom
  io.instr := rom(word_addr)

  // Assumes there is a file at path with
  // contents like
  // 12
  // deadbeef
  // 29381ad
  def parseRomContents():Array[BigInteger] = {
    val path = "generated/startup_program.hex"
    val source = scala.io.Source.fromFile(path)
    val lines = source.mkString
    source.close()
    return lines.split("\\n").map(new BigInteger(_, 16))
  }
}
