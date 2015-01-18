package TurboRav

import Chisel._
import Common._
import Constants._

////////////////////////////////////////
// Fetch
////////////////////////////////////////
class FetchIO() extends Bundle {
  val fch_dec = new FetchDecode()
  val exe_fch = new ExecuteFetch().flip()

  val requestResponseIo = new RequestResponseIo()
  val i_stall = Bool(INPUT)
}

class FetchDecode() extends Bundle {
  val instr_valid = Bool(OUTPUT)
  val instr       = UInt(OUTPUT, INSTRUCTION_WIDTH)
  val pc          = UInt(OUTPUT, Config.xlen)
}

////////////////////////////////////////
// Decode
////////////////////////////////////////
class DecodeIO() extends Bundle {
  val fch_dec = new FetchDecode().flip()
  val dec_exe = new DecodeExecute()
  val wrb_dec = new WritebackDecode().flip()

  //TODO: this one should be in HDU interface or something similar.
  val i_stall = Bool(INPUT)
}

class DecodeExecute() extends Bundle {
  val rs1     = UInt(OUTPUT, Config.xlen)
  val rs2     = UInt(OUTPUT, Config.xlen)
  val imm     = UInt(OUTPUT, Config.xlen)
  val rd_addr = UInt(OUTPUT, Config.xlen)
  val pc      = UInt(OUTPUT, Config.xlen)

  val exe_ctrl = new ExecuteCtrl()
  val mem_ctrl = new MemoryCtrl()
  val wrb_ctrl = new WritebackCtrl()
}

////////////////////////////////////////
// Execute
////////////////////////////////////////
class ExecuteIO() extends Bundle {
  val dec_exe = new DecodeExecute().flip()
  val exe_mem = new ExecuteMemory()
  val exe_fch = new ExecuteFetch()
  val fwu_exe = new ForwardingExecute().flip()

  val i_stall = Bool(INPUT)
}

class ExecuteCtrl() extends Bundle {
  val alu_in_a_sel = Bits(OUTPUT, ALU_IN_A_SEL_WIDTH)
  val alu_in_b_sel = Bits(OUTPUT, ALU_IN_B_SEL_WIDTH)
  val alu_func     = Bits(OUTPUT, ALU_FUNC_WIDTH)
  val bru_func     = Bits(OUTPUT, BRANCH_FUNC_WIDTH)
}

class ExecuteMemory() extends Bundle {
  val rd_addr    = UInt(OUTPUT, 5)
  val alu_result = UInt(OUTPUT, Config.xlen)
  val rs2        = UInt(OUTPUT, Config.xlen)
  val pc         = UInt(OUTPUT, Config.xlen)

  val mem_ctrl = new MemoryCtrl()
  val wrb_ctrl = new WritebackCtrl()
}

class ExecuteFetch() extends Bundle {
  val pc_sel = Bits(OUTPUT, PC_SEL_WIDTH)
  val pc_alu = UInt(OUTPUT, Config.xlen)
}

////////////////////////////////////////
// Memory
////////////////////////////////////////c
class MemoryIO() extends Bundle {
  val exe_mem = new ExecuteMemory().flip()
  val mem_wrb = new MemoryWriteback()
  val fwu_mem = new ForwardingMemory()

  val requestResponseIo = new RequestResponseIo()
  val i_stall = Bool(INPUT)
  val o_stall = Bool(OUTPUT)
}

class MemoryCtrl() extends Bundle {
  val write = Bool(OUTPUT)
  val read = Bool(OUTPUT)
}

class MemoryWriteback() extends Bundle {
  val rd_addr       = UInt(OUTPUT, Config.xlen)
  val alu_result    = UInt(OUTPUT, Config.xlen)
  val mem_read_data = UInt(OUTPUT, Config.xlen)
  val pc            = UInt(OUTPUT, Config.xlen)

  val wrb_ctrl = new WritebackCtrl()
}

////////////////////////////////////////
// Writeback
////////////////////////////////////////c
class WritebackIO() extends Bundle {
  val mem_wrb = new MemoryWriteback().flip()
  val wrb_dec = new WritebackDecode()
  val fwu_wrb = new ForwardingWriteback().flip()

  val i_stall = Bool(INPUT)
}

class WritebackCtrl() extends Bundle {
  val rd_wen = Bool(OUTPUT)
  val rd_sel = Bits(OUTPUT, RD_SEL_WIDTH)
}

class WritebackDecode() extends Bundle {
  val rd_wen  = Bool(OUTPUT)
  val rd_data = UInt(OUTPUT, Config.xlen)
  val rd_addr = UInt(OUTPUT, 5)
}

////////////////////////////////////////
// Forwarding Unit
////////////////////////////////////////
class ForwardingUnitIO() extends Bundle {
  val fwu_exe = new ForwardingExecute()
  val fwu_mem = new ForwardingMemory()
  val fwu_wrb = new ForwardingWriteback()
}

class ForwardingExecute() extends Bundle {
  val rs1_addr = UInt(INPUT, 5)
  val rs2_addr = UInt(INPUT, 5)
  val rs1_sel  = UInt(OUTPUT, RS_SEL_WIDTH)
  val rs2_sel  = UInt(OUTPUT, RS_SEL_WIDTH)
}

class ForwardingMemory()extends Bundle {
  val rd_addr = UInt(INPUT, 5)
  val rd_wen  = Bool(OUTPUT)
}

class ForwardingWriteback() extends Bundle {
  val rd_addr = UInt(INPUT, 5)
  val rd_wen  = Bool(OUTPUT)
}