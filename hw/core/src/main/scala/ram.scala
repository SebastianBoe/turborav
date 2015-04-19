package TurboRav

import Chisel._

import Common._
import Array._
import Apb._

class Ram extends Module {
  val io = new Bundle {
    // read or write address
    val addr   = UInt(INPUT, Config.xlen)

    // write
    val word_w   = UInt(INPUT, Config.xlen)
    val wen      = Bool(INPUT)
    val byte_en  = UInt(INPUT, Config.xlen)

    // read
    val ren    = Bool(INPUT)
    val word_r = UInt(OUTPUT, Config.xlen)
  }

  val wordSizeInBytes = Config.xlen / 8
  val numWords        = Config.ramSizeInBytes / wordSizeInBytes
  // val addrLSB         = log2Down(wordSizeInBytes)
  val addrLSB         = log2Down(wordSizeInBytes)
  val addrMSB         = addrLSB + log2Up(numWords)

  val ram = Mem(UInt(width = Config.xlen), numWords)

  val ramAddr = io.addr(addrMSB, addrLSB)

  io.word_r := UInt(0) // Default

  val maskByte     = Cat( Fill(Bool(false), 2*Config.xlen- 8),
                          Fill(Bool(true),    Config.xlen-24))

  val maskHalfword = Cat( Fill(Bool(false), 2*Config.xlen-16),
                          Fill(Bool(true),    Config.xlen-16))

  val maskWord     = Cat( Fill(Bool(false),   Config.xlen),
                          Fill(Bool(true),    Config.xlen))

  val byteOffset = io.addr(addrLSB-1, 0)

  when(io.wen) {
    val maskBits =  Mux(io.byte_en(0), maskByte,
                    Mux(io.byte_en(1), maskHalfword,
                                       maskWord
                    ))

    val doubleMask = maskBits  << (byteOffset * UInt(8))
    val doubleWord = io.word_w << (byteOffset * UInt(8))

    val wordHigh = doubleWord(2*Config.xlen-1, Config.xlen)
    val wordLow  = doubleWord(  Config.xlen,             0)

    val maskHigh = doubleMask(2*Config.xlen-1, Config.xlen)
    val maskLow  = doubleMask(  Config.xlen-1,           0)

    ram.write(ramAddr+UInt(1), wordHigh, maskHigh)
    ram.write(ramAddr        , wordLow,  maskLow )
  }
  .elsewhen(io.ren) {
    val readWordHigh = ram(ramAddr+UInt(1))
    val readWordLow  = ram(ramAddr)

    val doubleWord = Cat(readWordHigh, readWordLow)
    val word_shifted = doubleWord >> byteOffset

    val word =  Mux(io.byte_en(0), word_shifted & maskByte,
                Mux(io.byte_en(1), word_shifted & maskHalfword,
                                   word_shifted & maskWord
                ))

    io.word_r := word(Config.xlen-1, 0)
  }

}
