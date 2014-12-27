package TurboRav

import Chisel._
import Constants._
import Common._

class Cache() extends Module {

  val io = new Bundle {
    val write_address = UInt(INPUT, Config.xlen)
    val read_address = UInt(INPUT, Config.xlen)
    val write_data = UInt(INPUT, Config.xlen)
    val read_data = UInt(OUTPUT, Config.xlen)
    val read = Bool(INPUT)
    val write = Bool(INPUT)
    val hit = Bool(OUTPUT)
  }

  val read_address = Reg(init = UInt(0), next = io.read_address)

  // Extract configuration for convenience
  val cacheLineWidth = Config.cache.cacheLineWidth
  val numEntries     = Config.cache.numEntries
  val associativity  = Config.cache.associativity

  require(isPow2(numEntries))
  require(numEntries % associativity == 0)
  require(cacheLineWidth % INSTRUCTION_WIDTH == 0)

  val entriesPerBank = numEntries / associativity
  val instrPerCacheLine = cacheLineWidth / INSTRUCTION_WIDTH

  val byteOffsetSize  = log2Down(Config.xlen / BITS_IN_BYTE)
  val blockOffsetSize = log2Down(cacheLineWidth / INSTRUCTION_WIDTH)
  val indexSize = log2Down(entriesPerBank)
  val nonTagSize = indexSize + blockOffsetSize + byteOffsetSize
  val tagSize = INSTRUCTION_WIDTH - nonTagSize

  val read_tag = read_address(INSTRUCTION_WIDTH-1, nonTagSize)
  val read_index = read_address(nonTagSize-1, blockOffsetSize + byteOffsetSize)

  val tagArray = Mem(Bits(width = tagSize*associativity), entriesPerBank,
                     seqRead = true)
  val validArray = Reg(init = Bits(0, associativity * entriesPerBank))

  val read_lines= Vec.fill(associativity) { Bits() }
  val tagHits = Vec.fill(associativity) { Bool() }

  /* Linear Feedback register used for random replacement policy */
  val shift= Reg(init = UInt(1, 16))
  when (io.write) {
    val bit = shift(0)^shift(2)^shift(3)^shift(5)
    val shift_next = Cat(bit, shift(15,1))
    shift:= shift_next
  }

  for( i <- 0 until associativity) {
    val bank = Mem(Bits(width = cacheLineWidth), entriesPerBank,seqRead = true)
    val tag = tagArray(read_index)((i+1)*tagSize - 1, i*tagSize)
    // TODO: check valid bit
    tagHits(i) := tag === read_tag

    when(io.read){
      read_lines(i) := bank(read_index)
    } .otherwise {
      read_lines(i) := UInt(0)
    }

  }

  val read_line = Mux1H(tagHits, read_lines)
  //TODO: select out word from cache line
  io.read_data := read_line(Config.xlen-1,0)
  io.hit := tagHits.exists((x: Bool) => x === Bool(true))
}
