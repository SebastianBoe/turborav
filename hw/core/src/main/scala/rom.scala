package TurboRav

import Chisel._

import Common._
import Array._
import Apb._
import java.math.BigInteger;

class Rom() extends Module {
  val io = new SlaveToApbIo()

  // The apb bus addresses individual bytes, but the ROM stores 4-byte
  // words and assumes that all addresses are word-aligned. To go from
  // a byte-addressable address to a word addressable address we
  // right-shift twice.
  val word_addr = io.addr >> UInt(2)
  assert(io.addr(1,0) === UInt(0), "We assume word-aligned addresses.")

  val rom_array = parseRomContents()
  val rom = Vec(rom_array.map(UInt(_)))

  io.rdata  := clearIfDisabled(
    data    = rom(Reg(next = word_addr)),
    enabled = io.enable
  )

  val s_idle :: s_ready :: Nil = Enum(UInt(), 2)
  val state = Reg(init = s_idle)
  when( state === s_ready ){
    state := s_idle
  } .elsewhen ( io.sel ) {
    state := s_ready
  } .otherwise {
    state := s_idle
  }

  io.ready  := state === s_ready
  io.enable := io.ready

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
