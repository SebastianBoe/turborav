// Copyright (C) 2015 Sebastian Bøe, Joakim Andersson
// License: BSD 2-Clause (see LICENSE for details)

package TurboRav

import Chisel._
import Array._

class Ram extends Module {
  val io = new Bundle {
    // read or write address
    val addr   = UInt(INPUT, Config.xlen)

    // write
    val word_w   = UInt(INPUT, Config.xlen)
    val wen      = Bool(INPUT)
    val byte_en  = UInt(INPUT, 2)

    // read
    val ren    = Bool(INPUT)
    val word_r = UInt(OUTPUT, Config.xlen)
  }

  val word_size_in_bytes = Config.xlen / 8
  val num_words          = Config.ram_size_in_bytes / word_size_in_bytes

  val addr_lsb         = log2Down(word_size_in_bytes)
  val addr_msb         = addr_lsb + log2Up(num_words)

  val ram = Mem(UInt(width = Config.xlen), num_words)

  val ram_addr = io.addr(addr_msb, addr_lsb)

  val mask_byte     = UInt("h000000FF", width = 64)
  val mask_halfword = UInt("h0000FFFF", width = 64)
  val mask_word     = UInt("hFFFFFFFF", width = 64)

  val byte_offset = io.addr(addr_lsb-1, 0)

  val word_out = Reg(UInt())

  when(io.wen) {
    val mask_bits = Mux(io.byte_en(0), mask_byte,
                    Mux(io.byte_en(1), mask_halfword,
                                       mask_word
                    ))

    val double_mask = mask_bits << (byte_offset * UInt(8))
    val double_word = io.word_w << (byte_offset * UInt(8))

    val word_high = double_word(2*Config.xlen-1, Config.xlen)
    val word_low  = double_word(  Config.xlen,             0)

    val mask_high = double_mask(2*Config.xlen-1, Config.xlen)
    val mask_low  = double_mask(  Config.xlen-1,           0)

    ram.write(ram_addr+UInt(1), word_high, mask_high)
    ram.write(ram_addr        , word_low,  mask_low )
  }
  .elsewhen(io.ren) {
    val read_word_high = ram(ram_addr+UInt(1))
    val read_word_low  = ram(ram_addr)

    val double_word = Cat(read_word_high, read_word_low)
    val word_shifted = double_word >> (byte_offset * UInt(8))

    val word =  Mux(io.byte_en(0), word_shifted & mask_byte,
                Mux(io.byte_en(1), word_shifted & mask_halfword,
                                   word_shifted & mask_word
                ))

    word_out := word(Config.xlen-1, 0)
  }

  io.word_r := word_out

}
