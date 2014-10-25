package TurboRav

import Chisel._

class Cache(val cacheLineWidth: Int,
  val numEntries: Int,
  val associativity: Int) extends Module with Constants {

  val io = new Bundle {
    val address = UInt(INPUT, 32)
    val data    = UInt(OUTPUT, 32)
  }

  val INSTRUCTION_WIDTH_UINT = UInt(INSTRUCTION_WIDTH)

  require(isPowerOfTwo(numEntries))
  require(numEntries % associativity == 0)
  require(cacheLineWidth % INSTRUCTION_WIDTH == 0)

  val entriesPerBank = numEntries / associativity

  val dataBanks = Array.fill(associativity) {
    Mem(UInt(width = cacheLineWidth), entriesPerBank)
  }

  val numOffsetBits = log2(cacheLineWidth / INSTRUCTION_WIDTH)
  val numIndexBits  = log2(entriesPerBank)
  val numTagBits    = INSTRUCTION_WIDTH - numOffsetBits - numIndexBits

  val tagBanks = Array.fill(associativity) {
    new Bundle {
      val isValid = Mem(Bool(false),              entriesPerBank)
      val bank    = Mem(UInt(width = numTagBits), entriesPerBank)
    }
  }

  val instrOffset = if (numOffsetBits > 0) io.address(numOffsetBits - 1, 0) else UInt(0)
  val instrIndex  = io.address(numOffsetBits + numIndexBits - 1, numOffsetBits)
  val instrTag    = io.address(numOffsetBits + numIndexBits + numTagBits - 1,
    numOffsetBits + numIndexBits)

  when (tagBanks(0).bank(instrIndex) === instrTag
    && tagBanks(0).isValid(instrIndex)) {
      val upperInstrBitIndex = (instrOffset + UInt(1)) * INSTRUCTION_WIDTH_UINT- UInt(1)
      val lowerInstrBitIndex = instrOffset * INSTRUCTION_WIDTH_UINT
      io.data := dataBanks(0)(instrIndex)(upperInstrBitIndex, lowerInstrBitIndex)
    }

  // TODO: Use Chisel's standard library
  def isPowerOfTwo(n: Int): Boolean = (n & (n - 1)) == 0
  def log2(n: Int): Int = (scala.math.log(n) / scala.math.log(2)).toInt
}
