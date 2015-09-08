package TurboRav

import Chisel._

import Array._

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
  val next_addr = base_addr + UInt(1)
  val byte_addr = io.addr(addr_lsb - 1, 0)

  val bytes_out = Vec.fill(word_size_in_bytes) { Reg(init = UInt(0))}

  val mask = Mux(io.byte_en(0), UInt("b0001"),
             Mux(io.byte_en(1), UInt("b0011"),
                                UInt("b1111")
             ))

  val word_shifted = Vec.fill(word_size_in_bytes) { UInt()}
  val mask_shifted = Vec.fill(word_size_in_bytes) { UInt()}

  word_shifted(0) := io.word_w
  mask_shifted(0) := mask

  for(i <- 1 until word_size_in_bytes){
    /* Right rotate write word */
    val wsplit = xlen - (i * 8)
    word_shifted(i) := Cat(io.word_w(wsplit - 1,      0),
                           io.word_w(  xlen - 1, wsplit))

    /* Right rotate byte enable mask*/
    val msplit = word_size_in_bytes - i
    mask_shifted(i) := Cat(mask(            msplit - 1,      0),
                           mask(word_size_in_bytes - 1, msplit))
  }

  for(i <- 0 until word_size_in_bytes){
    /* Ram is kept in RAID0 configuration for FPGA performance
       reasons. There is one ram module per byte in a word */
    val ram_stripe = Mem(UInt(width = 8), num_words)

    val ram_addr = Mux(byte_addr > UInt(i), next_addr, base_addr)

    val enable_mask = mask_shifted(byte_addr)
    val write_word  = word_shifted(byte_addr)

    when( io.wen & enable_mask(i) ) {
      ram_stripe(ram_addr) := write_word(i * 8 + 7, i * 8)
    } .elsewhen( io.ren ) {
      bytes_out(i) := ram_stripe(ram_addr)
    }
  }

  val byte_addr_read = Reg(init = UInt(0))
  when(io.ren){
    byte_addr_read := byte_addr
  }
  val word_out = bytes_out.toBits()

  val word_out_shifted = Vec.fill(word_size_in_bytes) { UInt()}
  word_out_shifted(0) := word_out

  for(i <- 1 until word_size_in_bytes){
    /* Left rotate read word */
    val rsplit = i * 8
    word_out_shifted(i) := Cat(word_out(rsplit - 1,      0),
                               word_out(  xlen - 1, rsplit))
  }

  io.word_r := word_out_shifted(byte_addr_read)

}
