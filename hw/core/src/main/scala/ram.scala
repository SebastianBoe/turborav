package TurboRav

import Chisel._

import Common._
import Array._
import Apb._

class Ram extends Module {
  val io = new SlaveToApbIo()

  // TODO: Remove redundancy between APB slaves. Defining the io and
  // getting the word_addr is redundant. Perhaps inheritance is the
  // best tool for the job.

  val word_size_in_bytes = Config.apb_data_len / 8
  val byte_offset_size  = log2Down(word_size_in_bytes)
  val word_addr = io.addr >> UInt(byte_offset_size)

  // The 4 most siginificant bits segment the memory map,
  val ram_word_addr = io.addr(31 - 4, byte_offset_size)

  assert(io.addr(byte_offset_size - 1, 0) === UInt(0),
    "We assume word-aligned addresses."
  )

  val words = Config.ram_size_in_bytes / word_size_in_bytes
  val ram = Mem(
    UInt(width = Config.apb_data_len),
    words
  )

  // TODO: Remove statemachine redundancy between APB slaves. Perhaps
  // the apb slaves can have an RR interface like the masters have.
  val s_idle :: s_ready :: Nil = Enum(UInt(), 2)
  val state = Reg(init = s_idle)
  when( state === s_ready ){
    state := s_idle
  } .elsewhen ( io.sel ) {
    state := s_ready
  } .otherwise {
    state := s_idle
  }

  io.ready  := state === s_ready
  io.enable := io.ready

  // We need a one-cycle delay for some reason
  val ram_word_addr_prev = Reg(next = ram_word_addr)
  when(io.sel) {
    when(io.write){
      ram(ram_word_addr) := io.wdata
      io.rdata := UInt(0)
    }.otherwise {
      io.rdata := ram(ram_word_addr)
    }
  }.otherwise {
    io.rdata := UInt(0)
  }
}
