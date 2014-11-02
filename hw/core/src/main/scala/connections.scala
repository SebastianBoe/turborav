package TurboRav

import Chisel._
import Common._
import Constants._

class FetchDecode(implicit conf: TurboravConfig) extends Bundle {
  val instr_valid = Bool(OUTPUT)
  val instr       = UInt(OUTPUT, INSTRUCTION_WIDTH)
}

class DecodeExecute(implicit conf: TurboravConfig) extends Bundle {
  val rs1      = UInt(OUTPUT, conf.xlen)
  val rs2      = UInt(OUTPUT, conf.xlen)
  val imm      = UInt(OUTPUT, conf.xlen)
  val rd_addr  = UInt(OUTPUT, 5)

  val exe_ctrl = new ExecuteCtrl()
  val mem_ctrl = new MemoryCtrl()
  val wrb_ctrl  = new WritebackCtrl()
}

class ExecuteMemory(implicit conf: TurboravConfig) extends Bundle {
  val rd_addr    = UInt(OUTPUT, 5)
  val alu_result = UInt(OUTPUT, conf.xlen)
  val rs2        = UInt(OUTPUT, conf.xlen)

  val mem_ctrl = new MemoryCtrl()
  val wb_ctrl  = new WritebackCtrl()
}

class MemoryWriteback(implicit conf: TurboravConfig) extends Bundle {
  val rd_addr = UInt(OUTPUT, 5)

  val wb_ctrl = new WritebackCtrl()
}

class WritebackDecode(implicit conf: TurboravConfig) extends Bundle {
  val rd_wen  = Bool(OUTPUT)
  val rd_data = UInt(OUTPUT, conf.xlen)
  val rd_addr = UInt(OUTPUT, 5)
}

class ExecuteCtrl(implicit conf: TurboravConfig) extends Bundle {
  val alu_in_a_sel = Bits(OUTPUT, 2)
  val alu_in_b_sel = Bits(OUTPUT, 2)
  val alu_func     = Bits(OUTPUT, ALU_FUNC_WIDTH)
}

class MemoryCtrl(implicit conf: TurboravConfig) extends Bundle {
  val write = Bool(OUTPUT)
  val read = Bool(OUTPUT)
}

class WritebackCtrl(implicit conf: TurboravConfig) extends Bundle {
  val regbank_in_sel = Bits(OUTPUT, 2)
}

class FetchIO(implicit conf: TurboravConfig) extends Bundle {
  val fch_dec = new FetchDecode()
}

class DecodeIO(implicit conf: TurboravConfig) extends Bundle {
  val fch_dec = new FetchDecode().flip()
  val dec_exe = new DecodeExecute()
  val wrb_dec = new WritebackDecode().flip()

  //TODO: this one should be in HDU interface or something similar.
  val stall = Bool(INPUT)
}

class ExecuteIO(implicit conf: TurboravConfig) extends Bundle {
  val dec_exe = new DecodeExecute().flip()
  val exe_mem = new ExecuteMemory()
}

class MemoryIO(implicit conf: TurboravConfig) extends Bundle {
  val exe_mem = new ExecuteMemory().flip()
  val mem_wrb = new MemoryWriteback()
}

class WritebackIO(implicit conf: TurboravConfig) extends Bundle {
  val mem_wrb = new MemoryWriteback().flip()
  val wrb_dec = new WritebackDecode()
}
