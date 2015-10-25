package TurboRav

import Chisel._

class FpgaRamTest(c: FpgaRam) extends Tester(c) {
  // (uint32_t *)
  poke(c.io.addr, 4)
  poke(c.io.word_w, 1)
  poke(c.io.wen, 1)
  poke(c.io.byte_en, 0)
  poke(c.io.ren, 0)

  
}
