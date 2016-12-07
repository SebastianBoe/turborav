// Copyright (C) 2015 Sebastian Bøe, Joakim Andersson
// License: BSD 2-Clause (see LICENSE for details)

package TurboRav

import Chisel._

class FpgaRam extends Module {
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

  val xlen = Config.xlen

  val word_size_in_bytes = xlen / 8
  val num_words          = Config.ram_size_in_bytes / word_size_in_bytes

  val addr_lsb = log2Down(word_size_in_bytes)
  val addr_msb = addr_lsb + log2Up(num_words)

  val base_addr = io.addr(addr_msb, addr_lsb)
  val byte_addr = io.addr(addr_lsb - 1, 0)

  val is_halfword_access = io.byte_en(1)
  val is_byte_access     = io.byte_en(0)
  val is_word_access     = ! Any(is_halfword_access, is_byte_access)

  val byte_mask     = UInt("b0001")
  val halfword_mask = UInt("b0011")
  val word_mask     = UInt("b1111")

  val mask = MuxCase(
    word_mask,
    Array(
      is_byte_access     -> byte_mask,
      is_halfword_access -> halfword_mask
    )
  ) << byte_addr

  val bytes_out = Vec.fill(word_size_in_bytes) { Wire(UInt()) }
  val write_word  = io.word_w << (byte_addr * UInt(8))

  for(i <- 0 until word_size_in_bytes){
    /* Ram is kept in RAID0 configuration for FPGA performance
       reasons. There is one ram module per byte in a word */
    val ram_stripe = Mem(num_words, UInt(width = 8))

    when(mask(i)) {
      when(io.wen) {
        ram_stripe(base_addr) := write_word(i * 8 + 7, i * 8)
      } .elsewhen( io.ren ) {
        bytes_out(i) := ram_stripe(Reg(init = UInt(0), next = base_addr))
      }
    } .otherwise {
      bytes_out(i) := UInt(0)
    }
  }

  val word_out = bytes_out.toBits()
  //TODO: Investigate why chisel couldn't infer this width.
  word_out.setWidth(32)

  io.word_r := word_out >> RegNext(byte_addr * UInt(8))

}
