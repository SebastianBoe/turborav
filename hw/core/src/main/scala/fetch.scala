package TurboRav

import Chisel._
import Common._
import Constants._

class Fetch() extends Module {

  val io = new FetchIO()

  val pc = Reg(init = UInt(0, width = Config.xlen))
  val pc_next = pc + UInt(4)

  when(!io.stall){
    pc := pc_next
  }

  io.fch_dec.pc := pc
}