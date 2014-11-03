package TurboRav

import Chisel._

object Constants {

  val INSTRUCTION_WIDTH = 32

  val ALU_ADD_VAL  = 0
  val ALU_SLL_VAL  = 1
  val ALU_SLT_VAL  = 2
  val ALU_SLTU_VAL = 3
  val ALU_XOR_VAL  = 4
  val ALU_SRL_VAL  = 5
  val ALU_OR_VAL   = 6
  val ALU_AND_VAL  = 7
  val ALU_SUB_VAL  = 8
  val ALU_SRA_VAL  = 13

  val ALU_FUNC_WIDTH = 4
  val ALU_ADD     = UInt( ALU_ADD_VAL,    ALU_FUNC_WIDTH)
  val ALU_SLT     = UInt( ALU_SLT_VAL,    ALU_FUNC_WIDTH)
  val ALU_SLTU    = UInt( ALU_SLTU_VAL,   ALU_FUNC_WIDTH)
  val ALU_AND     = UInt( ALU_AND_VAL,    ALU_FUNC_WIDTH)
  val ALU_OR      = UInt( ALU_OR_VAL,     ALU_FUNC_WIDTH)
  val ALU_XOR     = UInt( ALU_XOR_VAL,    ALU_FUNC_WIDTH)
  val ALU_SUB     = UInt( ALU_SUB_VAL,    ALU_FUNC_WIDTH)
  val ALU_SRA     = UInt( ALU_SRA_VAL,    ALU_FUNC_WIDTH)
  val ALU_SLL     = UInt( ALU_SLL_VAL,    ALU_FUNC_WIDTH)
  val ALU_SRL     = UInt( ALU_SRL_VAL,    ALU_FUNC_WIDTH)

  val MULT_MUL_VAL    = 0
  val MULT_MULH_VAL   = 1
  val MULT_MULHSU_VAL = 2
  val MULT_MULHU_VAL  = 3
  val MULT_DIV_VAL    = 4
  val MULT_DIVU_VAL   = 5
  val MULT_REM_VAL    = 6
  val MULT_REMU_VAL   = 7

  val MULT_FUNC_WIDTH = 3
  val MULT_MUL    = UInt( MULT_MUL_VAL,    MULT_FUNC_WIDTH)
  val MULT_MULH   = UInt( MULT_MULH_VAL,   MULT_FUNC_WIDTH)
  val MULT_MULHU  = UInt( MULT_MULHU_VAL,  MULT_FUNC_WIDTH)
  val MULT_MULHSU = UInt( MULT_MULHSU_VAL, MULT_FUNC_WIDTH)
  val MULT_DIV    = UInt( MULT_DIV_VAL,    MULT_FUNC_WIDTH)
  val MULT_DIVU   = UInt( MULT_DIVU_VAL,   MULT_FUNC_WIDTH)
  val MULT_REM    = UInt( MULT_REM_VAL,    MULT_FUNC_WIDTH)
  val MULT_REMU   = UInt( MULT_REMU_VAL,   MULT_FUNC_WIDTH)

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
