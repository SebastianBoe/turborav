package TurboRav

import Chisel._

trait Constants {

  val INSTRUCTION_WIDTH = 32

  val ALU_ADD_VAL  = 1
  val ALU_SLT_VAL  = 2
  val ALU_SLTU_VAL = 3
  val ALU_AND_VAL  = 4
  val ALU_OR_VAL   = 5
  val ALU_XOR_VAL  = 6
  val ALU_SUB_VAL  = 7
  val ALU_SRA_VAL  = 8
  val ALU_SLL_VAL  = 9
  val ALU_SRL_VAL  = 10

  val ALU_ADD     = UInt( ALU_ADD_VAL,    4)
  val ALU_SLT     = UInt( ALU_SLT_VAL,    4)
  val ALU_SLTU    = UInt( ALU_SLTU_VAL,   4)
  val ALU_AND     = UInt( ALU_AND_VAL,    4)
  val ALU_OR      = UInt( ALU_OR_VAL,     4)
  val ALU_XOR     = UInt( ALU_XOR_VAL,    4)
  val ALU_SUB     = UInt( ALU_SUB_VAL,    4)
  val ALU_SRA     = UInt( ALU_SRA_VAL,    4)
  val ALU_SLL     = UInt( ALU_SLL_VAL,    4)
  val ALU_SRL     = UInt( ALU_SRL_VAL,    4)

  val MULT_MUL_VAL    = 0
  val MULT_MULH_VAL   = 1
  val MULT_MULHU_VAL  = 2
  val MULT_MULHSU_VAL = 3
  val MULT_DIV_VAL    = 4
  val MULT_DIVU_VAL   = 5
  val MULT_REM_VAL    = 6
  val MULT_REMU_VAL   = 7

  val MULT_MUL    = UInt( MULT_MUL_VAL,    3)
  val MULT_MULH   = UInt( MULT_MULH_VAL,   3)
  val MULT_MULHU  = UInt( MULT_MULHU_VAL,  3)
  val MULT_MULHSU = UInt( MULT_MULHSU_VAL, 3)
  val MULT_DIV    = UInt( MULT_DIV_VAL,    3)
  val MULT_DIVU   = UInt( MULT_DIVU_VAL,   3)
  val MULT_REM    = UInt( MULT_REM_VAL,    3)
  val MULT_REMU   = UInt( MULT_REMU_VAL,   3)

}
