package TurboRav

import Chisel._
import Common._
import Constants._

class FetchDecode() extends Bundle {
  val instr_valid = Bool(OUTPUT)
  val instr       = UInt(OUTPUT, INSTRUCTION_WIDTH)
  val pc          = UInt(OUTPUT, Config.xlen)
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

class ExecuteMemory() extends Bundle {
  val rd_addr    = UInt(OUTPUT, Config.xlen)
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

class MemoryWriteback() extends Bundle {
  val rd_addr       = UInt(OUTPUT, Config.xlen)
  val alu_result    = UInt(OUTPUT, Config.xlen)
  val mem_read_data = UInt(OUTPUT, Config.xlen)
  val pc            = UInt(OUTPUT, Config.xlen)

  val wrb_ctrl = new WritebackCtrl()
}

class WritebackDecode() extends Bundle {
  val rd_wen  = Bool(OUTPUT)
  val rd_data = UInt(OUTPUT, Config.xlen)
  val rd_addr = UInt(OUTPUT, 5)
}

class ExecuteCtrl() extends Bundle {
  val alu_in_a_sel = Bits(OUTPUT, ALU_IN_A_SEL_WIDTH)
  val alu_in_b_sel = Bits(OUTPUT, ALU_IN_B_SEL_WIDTH)
  val alu_func     = Bits(OUTPUT, ALU_FUNC_WIDTH)
  val bru_func     = Bits(OUTPUT, BRANCH_FUNC_WIDTH)
}

class MemoryCtrl() extends Bundle {
  val write = Bool(OUTPUT)
  val read = Bool(OUTPUT)
}

class WritebackCtrl() extends Bundle {
  val rd_wen = Bool(OUTPUT)
  val rd_sel = Bits(OUTPUT, RD_SEL_WIDTH)
}

class FetchIO() extends Bundle {
  val fch_dec = new FetchDecode()
  val exe_fch = new ExecuteFetch().flip()

  val stall = Bool(INPUT)
}

class DecodeIO() extends Bundle {
  val fch_dec = new FetchDecode().flip()
  val dec_exe = new DecodeExecute()
  val wrb_dec = new WritebackDecode().flip()

  //TODO: this one should be in HDU interface or something similar.
  val stall = Bool(INPUT)
}

class ExecuteIO() extends Bundle {
  val dec_exe = new DecodeExecute().flip()
  val exe_mem = new ExecuteMemory()
  val exe_fch = new ExecuteFetch()

  val stall = Bool(INPUT)
}

class MemoryIO() extends Bundle {
  val exe_mem = new ExecuteMemory().flip()
  val mem_wrb = new MemoryWriteback()

  val stall = Bool(INPUT)
}

class WritebackIO() extends Bundle {
  val mem_wrb = new MemoryWriteback().flip()
  val wrb_dec = new WritebackDecode()

  val stall = Bool(INPUT)
}
