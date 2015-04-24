package TurboRav

import Chisel._
import Constants._
import Common._

class Cache() extends Module {

  val io = new Bundle {
    val write_address = UInt(INPUT, Config.xlen)
    val read_address  = UInt(INPUT, Config.xlen)
    val write_data    = UInt(INPUT, Config.xlen)
    val read_data     = UInt(OUTPUT, Config.xlen)
    val read          = Bool(INPUT)
    val write         = Bool(INPUT)
    val hit           = Bool(OUTPUT)
  }

  val read_address = Reg(init = UInt(0), next = io.read_address)

  // Extract configuration for convenience
  val cache_line_width = Config.cache.cache_line_width
  val num_entries     = Config.cache.num_entries
  val associativity  = Config.cache.associativity

  require(isPow2(num_entries))
  require(num_entries % associativity == 0)
  require(cache_line_width % INSTRUCTION_WIDTH == 0)

  val entries_per_bank  = num_entries / associativity
  val instrPerCacheLine = cache_line_width / INSTRUCTION_WIDTH

  val byte_offset_size  = log2Down(Config.xlen / BITS_IN_BYTE)
  val block_offset_size = log2Down(cache_line_width / INSTRUCTION_WIDTH)
  val index_size        = log2Down(entries_per_bank)
  val non_tag_size      = index_size + block_offset_size + byte_offset_size
  val tag_size          = INSTRUCTION_WIDTH - non_tag_size

  val read_tag   = read_address(INSTRUCTION_WIDTH-1, non_tag_size)
  val read_index = read_address(non_tag_size-1,
                                block_offset_size + byte_offset_size)

  val tag_array  = Mem( Bits(width = tag_size*associativity), entries_per_bank,
                        seqRead = true)
  val validArray = Reg(init = Bits(0, associativity * entries_per_bank))

  val read_lines = Vec.fill(associativity) { Bits() }
  val tag_hits   = Vec.fill(associativity) { Bool() }

  /* Linear Feedback register used for random replacement policy */
  val shift = LFSR16(io.write)

  for( i <- 0 until associativity) {
    val bank = Mem(Bits(width = cache_line_width),
                  entries_per_bank,
                  seqRead = true)
    val tag = tag_array(read_index)((i+1)*tag_size - 1, i*tag_size)
    // TODO: check valid bit
    tag_hits(i) := tag === read_tag

    when(io.read){
      read_lines(i) := bank(read_index)
    } .otherwise {
      read_lines(i) := UInt(0)
    }

  }

  val read_line = Mux1H(tag_hits, read_lines)
  //TODO: select out word from cache line
  io.read_data := read_line(Config.xlen-1,0)
  io.hit := tag_hits.exists((x: Bool) => x === Bool(true))
}
