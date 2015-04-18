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
  val hdu_fch = new HazardDetectionUnitFetch().flip()

  val requestResponseIo = new RequestResponseIo()
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
  val hdu_dec = new HazardDetectionUnitDecode().flip()
}

class DecodeExecute() extends Bundle {
  val rs1_addr= UInt(OUTPUT, 5)
  val rs1     = UInt(OUTPUT, Config.xlen)
  val rs2_addr= UInt(OUTPUT, 5)
  val rs2     = UInt(OUTPUT, Config.xlen)
  val imm     = UInt(OUTPUT, Config.xlen)
  val rd_addr = UInt(OUTPUT, Config.xlen)
  val pc      = UInt(OUTPUT, Config.xlen)
  val pc_sel  = Bits(INPUT,  PC_SEL_WIDTH)

  val exe_ctrl = new ExecuteCtrl()
  val mem_ctrl = new MemoryCtrl()
  val wrb_ctrl = new WritebackCtrl()

  def kill()
  {
    exe_ctrl.kill()
    wrb_ctrl.kill()
    mem_ctrl.kill()
  }
}

////////////////////////////////////////
// Execute
////////////////////////////////////////
class ExecuteIO() extends Bundle {
  val dec_exe = new DecodeExecute().flip()
  val exe_mem = new ExecuteMemory()
  val exe_fch = new ExecuteFetch()
  val fwu_exe = new ForwardingExecute().flip()
  val mem_exe = new MemoryExecute().flip()
  val wrb_exe = new WritebackExecute().flip()
  val hdu_exe = new HazardDetectionUnitExecute().flip()
}

class ExecuteCtrl() extends Bundle {
  val alu_in_a_sel = Bits(OUTPUT, ALU_IN_A_SEL_WIDTH)
  val alu_in_b_sel = Bits(OUTPUT, ALU_IN_B_SEL_WIDTH)
  val alu_func     = Bits(OUTPUT, ALU_FUNC_WIDTH)
  val bru_func     = Bits(OUTPUT, BRANCH_FUNC_WIDTH)
  val mult_func    = Bits(OUTPUT, MULT_FUNC_WIDTH)
  val mult_enable  = Bool(OUTPUT)

  def kill(){
    bru_func := BNOT
  }
}

class ExecuteMemory() extends Bundle {
  val rd_addr    = UInt(OUTPUT, 5)
  val alu_result = UInt(OUTPUT, Config.xlen)
  val rs2        = UInt(OUTPUT, Config.xlen)
  val pc         = UInt(OUTPUT, Config.xlen)

  val mem_ctrl = new MemoryCtrl()
  val wrb_ctrl = new WritebackCtrl()

  def kill()
  {
    wrb_ctrl.kill()
    mem_ctrl.kill()
  }

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
  val fwu_mem = new ForwardingMemory().flip()
  val mem_exe = new MemoryExecute()
  val hdu_mem = new HazardDetectionUnitMemory().flip()

  val requestResponseIo = new RequestResponseIo()
  val i_stall = Bool(INPUT)
  val o_stall = Bool(OUTPUT)
}

class MemoryCtrl() extends Bundle {
  val write = Bool(OUTPUT)
  val read = Bool(OUTPUT)
  val mem_width = UInt(OUTPUT, width = 3)

  def kill() = {
    write := Bool(false)
    read := Bool(false)
    mem_width := UInt(0)
  }
}

class MemoryWriteback() extends Bundle {
  val rd_addr       = UInt(OUTPUT, Config.xlen)
  val alu_result    = UInt(OUTPUT, Config.xlen)
  val mem_read_data = UInt(OUTPUT, Config.xlen)
  val pc            = UInt(OUTPUT, Config.xlen)

  val wrb_ctrl = new WritebackCtrl()
}

class MemoryExecute() extends Bundle {
  val alu_result = UInt(OUTPUT, Config.xlen)
}

////////////////////////////////////////
// Writeback
////////////////////////////////////////c
class WritebackIO() extends Bundle {
  val mem_wrb = new MemoryWriteback().flip()
  val wrb_dec = new WritebackDecode()
  val fwu_wrb = new ForwardingWriteback().flip()
  val wrb_exe = new WritebackExecute()
  val hdu_wrb = new HazardDetectionUnitWriteback().flip()

  val i_stall = Bool(INPUT)
}

class WritebackCtrl() extends Bundle {
  val rd_wen = Bool(OUTPUT)
  val rd_sel = Bits(OUTPUT, RD_SEL_WIDTH)

  def kill() = {
    rd_wen := Bool(false)
  }

}

class WritebackExecute() extends Bundle {
  val rd_data = UInt(OUTPUT, Config.xlen)
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
  val rd_wen  = Bool(INPUT)
}

class ForwardingWriteback() extends Bundle {
  val rd_addr = UInt(INPUT, 5)
  val rd_wen  = Bool(INPUT)
}

////////////////////////////////////////
// Hazard Detection Unit
////////////////////////////////////////
class HazardDetectionUnitIO() extends Bundle {
  val hdu_fch = new HazardDetectionUnitFetch()
  val hdu_dec = new HazardDetectionUnitDecode()
  val hdu_exe = new HazardDetectionUnitExecute()
  val hdu_mem = new HazardDetectionUnitMemory()
  val hdu_wrb = new HazardDetectionUnitWriteback()
}

class HazardDetectionUnitFetch() extends Bundle {
  val instructionValid = Bool(INPUT)

  val stall = Bool(OUTPUT)
}

class HazardDetectionUnitDecode() extends Bundle {
  val stall = Bool(OUTPUT)
}

class HazardDetectionUnitExecute extends Bundle {
  val mult_busy = Bool(INPUT)
  val rs1_addr  = UInt(INPUT, 5)
  val rs2_addr  = UInt(INPUT, 5)

  val stall     = Bool(OUTPUT)
}

class HazardDetectionUnitMemory extends Bundle {
  val mem_read = Bool(INPUT)
  val rd_addr  = UInt(INPUT, 5)

  val stall    = Bool(OUTPUT)
}

class HazardDetectionUnitWriteback extends Bundle {
  val stall = Bool(OUTPUT)
}
