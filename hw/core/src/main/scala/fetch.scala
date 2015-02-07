package TurboRav

import Chisel._
import Common._
import Constants._

class Fetch() extends Module {
  val io = new FetchIO()

  val rr = io.requestResponseIo // For convenience

  val pc = Reg(init = UInt(0, width = Config.xlen))

  /* There should be a better way */
  val take_saved_branch = Reg(init = Bool(false))
  val saved_branch_addr = Reg(init = UInt(0))

  val pc_next = Mux(io.exe_fch.pc_sel === PC_SEL_BRJMP, io.exe_fch.pc_alu,
                Mux(take_saved_branch,                  saved_branch_addr,
                                                        pc + UInt(4)
                ))

  val should_stall = io.i_stall || !rr.response.valid
  val has_branched = io.exe_fch.pc_sel === PC_SEL_BRJMP || take_saved_branch

  when(io.exe_fch.pc_sel === PC_SEL_BRJMP){
    take_saved_branch := Bool(true)
    saved_branch_addr := io.exe_fch.pc_alu
  }

  when(!should_stall){
    pc := pc_next
    take_saved_branch := Bool(false)
  }

  // Fetch to decode
  io.fch_dec.pc          := pc
  io.fch_dec.instr_valid := rr.response.valid && !has_branched
  io.fch_dec.instr       := rr.response.bits.word

  // Memory interface to fetch
  rr.request.bits.addr := pc
  rr.request.valid := Bool(true) // I think this is safe.
  rr.request.bits.wdata := UInt(0)
  rr.request.bits.write := Bool(false)
}
