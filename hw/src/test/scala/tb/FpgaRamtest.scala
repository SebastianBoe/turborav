package TurboRav

import Chisel._

class FpgaRamTest(c: FpgaRam) extends JUnitTester(c) {
  def write_and_then_read_back(addr: Long, word: Long, word_length_bytes: Int): Unit = {
    println("write_and_then_read_back(addr: %x, word: %x, word_length_bytes: %x)".format(
      addr, word, word_length_bytes
      )
    )

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

    if(is_unaligned_access(addr, word_length_bytes)){
      unaligned_access_test(addr, word, byte_en, mask)
    } else {
      aligned_access_test  (addr, word, byte_en, mask)
    }
  }

  def aligned_access_test(addr: Long, word: Long, byte_en: Long, mask: Long) = {
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

  def unaligned_access_test(addr: Long, word: Long, byte_en: Long, mask: Long) = {
    println("Skip test of unaligned accesses until it is supported.")
  }

  def is_unaligned_access(addr: Long, word_length_bytes: Int) = {
    addr % word_length_bytes != 0
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
