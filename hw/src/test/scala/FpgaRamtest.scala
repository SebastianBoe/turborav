package TurboRav

import Chisel._

class FpgaRamTest(c: FpgaRam) extends Tester(c) {
  def write_and_then_read_back(addr: Long, word: Long, word_length_bytes: Int) = {
    println("write_and_then_read_back(%x, %x, %x)".format(addr, word, word_length_bytes))

    //TODO: Check that we aren't modifying any memory contents that we
    //shouldn't be.

    // *addr = value;
    // expect value == *addr;

    val byte_en = word_length_bytes match {
      case 1 => 1
      case 2 => 2
      case 4 => 0
    }
    val mask = word_length_bytes match {
      case 1 => 0xFFL
      case 2 => 0xFFFFL
      case 4 => 0xFFFFFFFFL
    }

    poke(c.io.addr, addr)
    poke(c.io.word_w, word)
    poke(c.io.wen, 1)
    poke(c.io.byte_en, byte_en)
    poke(c.io.ren, 0)

    step(1)

    poke(c.io.addr, addr)
    poke(c.io.word_w, 0)
    poke(c.io.wen, 0)
    poke(c.io.byte_en, byte_en)
    poke(c.io.ren, 1)

    step(1)

    expect(c.io.word_r, word & mask)
  }

  for {
    addr              <- 0 to 16
    word              <- Array(0, 1, 0x10101010L, 0x439820L)
    word_length_bytes <- Array(1, 2, 4)
  } write_and_then_read_back(
    addr              = addr,
    word              = word,
    word_length_bytes = word_length_bytes
  )
}
