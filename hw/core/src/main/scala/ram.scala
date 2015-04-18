package TurboRav

import Chisel._

import Common._
import Array._
import Apb._

class Ram extends Module {
  val io = new Bundle {
    val addr   = UInt(INPUT, Config.xlen)
    val word_w = UInt(INPUT, Config.xlen)
    val wen    = Bool(INPUT)

    val word_r = UInt(OUTPUT, Config.xlen)
  }

  val word_size_in_bytes           = Config.xlen / 8
  val byte_offset_size             = log2Down(word_size_in_bytes)
  val words                        = Config.ram_size_in_bytes / word_size_in_bytes
  val most_significant_address_bit = byte_offset_size + log2Up(words)

  val ram = Mem(
    UInt(width = Config.xlen),
    words
  )

  val ram_addr = io.addr(
    most_significant_address_bit,
    byte_offset_size
  )

  io.word_r := UInt(0) // Default
  when(io.wen) {
    ram(ram_addr) := io.word_w
  } .otherwise {
    io.word_r := ram(ram_addr)
  }
}
