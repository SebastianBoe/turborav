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

  val mask = Mux(io.byte_en(0), UInt("b0001"),
             Mux(io.byte_en(1), UInt("b0011"),
                                UInt("b1111")
             ))

  val bytes_out = Vec.fill(word_size_in_bytes) { UInt() }

  val word_shifted = Vec.fill(word_size_in_bytes) { UInt()}
  val mask_shifted = Vec.fill(word_size_in_bytes) { UInt()}
  for(i <- 0 until word_size_in_bytes){
    word_shifted(i) := LeftRotate(io.word_w , i * 8)
    mask_shifted(i) := LeftRotate(mask      , i    )
  }

  for(i <- 0 until word_size_in_bytes){
    /* Ram is kept in RAID0 configuration for FPGA performance
       reasons. There is one ram module per byte in a word */
    val ram_stripe = Mem(UInt(width = 8), num_words)
    val ram_addr = base_addr + UInt(byte_addr > UInt(i))

    val enable_mask = mask_shifted(byte_addr)
    val write_word  = word_shifted(byte_addr)

    when(enable_mask(i)) {
      when(io.wen) {
        ram_stripe(ram_addr) := write_word(i * 8 + 7, i * 8)
      } .elsewhen( io.ren ) {
        bytes_out(i) := ram_stripe(Reg(init = UInt(0), next = ram_addr))
      }
    } .otherwise {
      bytes_out(i) := UInt(0)
    }
  }

  val word_out = bytes_out.toBits()
  //TODO: Investigate why chisel couldn't infer this width.
  word_out.setWidth(32)

  val word_out_shifted = Vec.fill(word_size_in_bytes) { UInt()}
  for(i <- 0 until word_size_in_bytes){
    word_out_shifted(i) := RightRotate(word_out, i * 8)
  }

  io.word_r := word_out_shifted(
    Reg(init = UInt(0), next = byte_addr)
  )
}
