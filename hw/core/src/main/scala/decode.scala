package TurboRav

import Chisel._
import Common._

object Opcodes {

  def OPCODE_LUI        = Bits("b0110111")
  def OPCODE_AUIPC      = Bits("b0010111")
  def OPCODE_JAL        = Bits("b1101111")
  def OPCODE_JALR       = Bits("b1100111")
  def OPCODE_BRANCH     = Bits("b1100011")
  def OPCODE_LOAD       = Bits("b0000011")
  def OPCODE_STORE      = Bits("b0100011")
  def OPCODE_REG_REG    = Bits("b0110011")
  def OPCODE_REG_IMM    = Bits("b0010011")
  def OPCODE_FENCE      = Bits("b0001111")
  def OPCODE_SYSTEM     = Bits("b1110011")

}

class Decode(implicit conf: TurboravConfig) extends Module with Constants {
  val io = new Bundle(){

    val stall = Bool(INPUT)

    val instr_valid = Bool(INPUT)
    val instr       = UInt(INPUT, INSTRUCTION_WIDTH)

    // Write Back signal
    val wb_rd_wen   = Bool(INPUT)
    val wb_rd_addr  = UInt(INPUT, log2Up(conf.xlen))
    val wb_rd_data  = UInt(INPUT, conf.xlen)

    // Data Path Signals
    val dec_rs1      = UInt(OUTPUT, conf.xlen)
    val dec_rs2      = UInt(OUTPUT, conf.xlen)
    val dec_rd_addr  = UInt(OUTPUT, log2Up(conf.xlen))
    val dec_imm      = UInt(OUTPUT, conf.xlen)

    // Control Path Signals
    val dec_alu_func = UInt(OUTPUT, ALU_FUNC_WIDTH)

  }

  val rs1_addr = io.instr(19, 15)
  val rs2_addr = io.instr(24, 20)

  val opcode = io.instr(6, 0)

  //Sign extended immediates
  val imm_i = Cat(Fill(io.instr(31), 20), io.instr(31, 20))
  val imm_s = Cat(Fill(io.instr(31), 20), Cat(io.instr(31, 25), io.instr(11, 7)))
  val imm_b = Cat(Fill(io.instr(31), 19),
                  io.instr(7), io.instr(30, 25), io.instr(11, 8),
                  UInt(0, width = 1))
  val imm_u = Cat(io.instr(31, 20), UInt(0, width = 20))
  val imm_j = Cat(Fill(io.instr(31), 12),
                  io.instr(19, 12), io.instr(20), io.instr(30, 21),
                  UInt(0, width = 1))

  val imm_z = UInt(0, width = conf.xlen)

  val regbank = Module(new RegBank())
  regbank.io.rs1_addr := rs1_addr
  regbank.io.rs2_addr := rs2_addr
  regbank.io.rd_addr  := io.wb_rd_addr
  regbank.io.rd_data  := io.wb_rd_data
  regbank.io.rd_wen   := io.wb_rd_wen

  val imm            = Reg(init = UInt(0))
  val stage_alu_func = Reg(init = UInt(0))
  val rd_addr        = Reg(init = UInt(0))
  val rs1_data       = Reg(init = UInt(0))
  val rs2_data       = Reg(init = UInt(0))

  when(io.instr_valid && !io.stall){
    when(io.instr === Opcodes.OPCODE_REG_REG) {
      stage_alu_func := UInt(ALU_ADD_VAL)
      imm := imm_z
    } .elsewhen(io.instr === Opcodes.OPCODE_REG_IMM){
      imm := imm_i
    } .elsewhen(io.instr === Opcodes.OPCODE_STORE){
      imm := imm_s
    }

    rs1_data := regbank.io.rs1_data
    rs2_data := regbank.io.rs2_data
    rd_addr  := io.instr(11, 6)
  }

  io.dec_imm := imm
  io.dec_alu_func := stage_alu_func
  io.dec_rd_addr := rd_addr
  io.dec_rs1 := rs1_data
  io.dec_rs2 := rs2_data
}