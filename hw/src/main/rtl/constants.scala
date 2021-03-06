// Copyright (C) 2015 Sebastian Bøe, Joakim Andersson
// License: BSD 2-Clause (see LICENSE for details)

package TurboRav

import Chisel._
import java.math.BigInteger

object Constants {

  val INSTRUCTION_WIDTH = 32
  val BITS_IN_BYTE = 8

  val BASE_ADDR_ROM  = new BigInteger("00000000", 16)
  val BASE_ADDR_RAM  = new BigInteger("10000000", 16)
  val BASE_ADDR_APB  = new BigInteger("20000000", 16)

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

  val BRANCH_BEQ_VAL  = 0
  val BRANCH_BNE_VAL  = 1
  val BRANCH_BLT_VAL  = 4
  val BRANCH_BGE_VAL  = 5
  val BRANCH_BLTU_VAL = 6
  val BRANCH_BGEU_VAL = 7
  val BRANCH_BNOT_VAL = 2
  val BRANCH_BJMP_VAL = 3

  val BRANCH_FUNC_WIDTH = 3
  val BEQ  = UInt(BRANCH_BEQ_VAL,  BRANCH_FUNC_WIDTH)
  val BNE  = UInt(BRANCH_BNE_VAL,  BRANCH_FUNC_WIDTH)
  val BLT  = UInt(BRANCH_BLT_VAL,  BRANCH_FUNC_WIDTH)
  val BGE  = UInt(BRANCH_BGE_VAL,  BRANCH_FUNC_WIDTH)
  val BLTU = UInt(BRANCH_BLTU_VAL, BRANCH_FUNC_WIDTH)
  val BGEU = UInt(BRANCH_BGEU_VAL, BRANCH_FUNC_WIDTH)
  val BNOT = UInt(BRANCH_BNOT_VAL, BRANCH_FUNC_WIDTH)
  val BJMP = UInt(BRANCH_BJMP_VAL, BRANCH_FUNC_WIDTH)

  val OPCODE_LUI        = Bits("b0110111")
  val OPCODE_AUIPC      = Bits("b0010111")
  val OPCODE_JAL        = Bits("b1101111")
  val OPCODE_JALR       = Bits("b1100111")
  val OPCODE_BRANCH     = Bits("b1100011")
  val OPCODE_LOAD       = Bits("b0000011")
  val OPCODE_STORE      = Bits("b0100011")
  val OPCODE_REG_REG    = Bits("b0110011")
  val OPCODE_REG_IMM    = Bits("b0010011")
  val OPCODE_FENCE      = Bits("b0001111")
  val OPCODE_SYSTEM     = Bits("b1110011")
  val OPCODE_MULT_DIV   = Bits("b0110011")

  val ALU_IN_A_RS1_VAL  = 0
  val ALU_IN_A_PC_VAL   = 1
  val ALU_IN_A_ZERO_VAL = 2

  val ALU_IN_A_SEL_WIDTH = 2
  val ALU_IN_A_RS1  = UInt(ALU_IN_A_RS1_VAL,  ALU_IN_A_SEL_WIDTH)
  val ALU_IN_A_PC   = UInt(ALU_IN_A_PC_VAL,   ALU_IN_A_SEL_WIDTH)
  val ALU_IN_A_ZERO = UInt(ALU_IN_A_ZERO_VAL, ALU_IN_A_SEL_WIDTH)

  val ALU_IN_B_RS2_VAL = 0
  val ALU_IN_B_IMM_VAL = 1

  val ALU_IN_B_SEL_WIDTH = 2
  val ALU_IN_B_RS2 = UInt(ALU_IN_B_RS2_VAL, ALU_IN_B_SEL_WIDTH)
  val ALU_IN_B_IMM = UInt(ALU_IN_B_IMM_VAL, ALU_IN_B_SEL_WIDTH)

  val PC_SEL_PC_PLUS4_VAL  = 0
  val PC_SEL_BRJMP_VAL     = 1

  val PC_SEL_WIDTH = 1
  val PC_SEL_PC_PLUS4 = UInt(PC_SEL_PC_PLUS4_VAL, PC_SEL_WIDTH)
  val PC_SEL_BRJMP    = UInt(PC_SEL_BRJMP_VAL, PC_SEL_WIDTH)

  val RD_ALU_VAL = 0
  val RD_MEM_VAL = 1
  val RD_PC_VAL  = 2

  val RD_SEL_WIDTH = 2
  val RD_ALU = UInt(RD_ALU_VAL, RD_SEL_WIDTH)
  val RD_MEM = UInt(RD_MEM_VAL, RD_SEL_WIDTH)
  val RD_PC  = UInt(RD_PC_VAL,  RD_SEL_WIDTH)

  val RS_SEL_WIDTH = 2
  val RS_SEL_DEC_VAL = 0
  val RS_SEL_MEM_VAL = 1
  val RS_SEL_WRB_VAL = 2

  val RS_SEL_DEC = UInt(RS_SEL_DEC_VAL, RS_SEL_WIDTH)
  val RS_SEL_MEM = UInt(RS_SEL_MEM_VAL, RS_SEL_WIDTH)
  val RS_SEL_WRB = UInt(RS_SEL_WRB_VAL, RS_SEL_WIDTH)

  val SPI_TX_BYTE_REG_ADDR = UInt(0, Config.apb_addr_len - 4)

  val MEMORY_SEGMENT_ROM = UInt("b0000", 4)
  val MEMORY_SEGMENT_RAM = UInt("b0001", 4)
  val MEMORY_SEGMENT_APB = UInt("b0010", 4)
  val MEMORY_SEGMENT_SPI = MEMORY_SEGMENT_APB // Only true now because
                                              // SPI is the only APB
                                              // peripheral
}
