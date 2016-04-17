package TurboRav

import Chisel._
import Constants._

class DecodeTest(c: Decode) extends JUnitTester(c) {
  private def signed(a: Long) = { a & 0x00000000ffffffffL }

  private def testFunc(instr: Long,
                exp_func: Long,
                rs_addr: Int,
                is_imm: Boolean = false,
                exp_imm: Long = 0) {
    poke(c.io.fch_dec.instr, instr)
    step(1)
    expect(c.io.dec_exe.exe_ctrl.alu_func, exp_func)
    expect(c.io.dec_exe.exe_ctrl.bru_func, BRANCH_BNOT_VAL)
    expect(c.io.dec_exe.exe_ctrl.alu_in_a_sel, ALU_IN_A_RS1_VAL)
    expect(c.io.dec_exe.wrb_ctrl.rd_sel, RD_ALU_VAL)
    expect(c.io.dec_exe.rd_addr, rs_addr)
    expect(c.io.dec_exe.wrb_ctrl.rd_wen, 1)
    if (is_imm){
      expect(c.io.dec_exe.imm, signed(exp_imm))
      expect(c.io.dec_exe.exe_ctrl.alu_in_b_sel, ALU_IN_B_IMM_VAL)
    } else {
      expect(c.io.dec_exe.exe_ctrl.alu_in_b_sel, ALU_IN_B_RS2_VAL)
    }
  }

  private def testBranch(instr: Long,
                  exp_func: Long,
                  exp_imm: Long) {
    poke(c.io.fch_dec.instr, instr)
    step(1)
    expect(c.io.dec_exe.exe_ctrl.bru_func, exp_func)
    expect(c.io.dec_exe.exe_ctrl.alu_func, ALU_ADD_VAL)
    expect(c.io.dec_exe.exe_ctrl.alu_in_a_sel, ALU_IN_A_PC_VAL)
    expect(c.io.dec_exe.wrb_ctrl.rd_wen, 0)
    expect(c.io.dec_exe.imm, signed(exp_imm))
    expect(c.io.dec_exe.exe_ctrl.alu_in_b_sel, ALU_IN_B_IMM_VAL)
  }

  private def testJump(instr: Long,
                exp_imm: Long,
                is_jalr: Boolean = false,
                rd_addr: Int = 0) {
    poke(c.io.fch_dec.instr, instr)
    step(1)
    expect(c.io.dec_exe.exe_ctrl.bru_func, BRANCH_BJMP_VAL)
    expect(c.io.dec_exe.exe_ctrl.alu_func, ALU_ADD_VAL)
    if(is_jalr){
      expect(c.io.dec_exe.exe_ctrl.alu_in_a_sel, ALU_IN_A_RS1_VAL)
      expect(c.io.dec_exe.rd_addr, rd_addr)
    } else {
      expect(c.io.dec_exe.exe_ctrl.alu_in_a_sel, ALU_IN_A_PC_VAL)
    }

    expect(c.io.dec_exe.wrb_ctrl.rd_sel, RD_PC_VAL)
    expect(c.io.dec_exe.wrb_ctrl.rd_wen, 1)
    expect(c.io.dec_exe.imm, signed(exp_imm))
    expect(c.io.dec_exe.exe_ctrl.alu_in_b_sel, ALU_IN_B_IMM_VAL)
  }

  private def testUpper(instr: Long,
                 exp_imm: Long,
                 with_pc: Boolean) {
    poke(c.io.fch_dec.instr, instr)
    step(1)
    expect(c.io.dec_exe.exe_ctrl.bru_func, BRANCH_BNOT_VAL)
    expect(c.io.dec_exe.exe_ctrl.alu_func, ALU_ADD_VAL)
    if(with_pc){
      expect(c.io.dec_exe.exe_ctrl.alu_in_a_sel, ALU_IN_A_PC_VAL)
    } else {
      expect(c.io.dec_exe.exe_ctrl.alu_in_a_sel, ALU_IN_A_ZERO_VAL)
    }
    expect(c.io.dec_exe.wrb_ctrl.rd_sel, RD_ALU_VAL)
    expect(c.io.dec_exe.wrb_ctrl.rd_wen, 1)
    expect(c.io.dec_exe.imm, signed(exp_imm))
    expect(c.io.dec_exe.exe_ctrl.alu_in_b_sel, ALU_IN_B_IMM_VAL)
  }

  // Expect that the decode stage will pipeline registers
  // correctly. Specifically, it will retain it's pipeline register
  // contents when stalled.
  private def testStall() {
    poke(c.io.fch_dec.pc, 42)

    step(1)
    poke(c.io.fch_dec.pc, 1)
    poke(c.io.hdu_dec.stall, 1)

    step(1)
    poke(c.io.hdu_dec.stall, 0)
    expect(c.io.dec_exe.pc, 42)
  }

  val  add_instr1 = 0x7ff30193L   //    addi   x3, x6, 2047
  val  add_instr2 = 0x80038213L   //    addi   x4, x7, -2048

  val add_instr   = 0x00b483b3L   //    add    x7, x9, x11
  val slt_instr   = 0x00b4a3b3L   //    slt    x7, x9, x11
  val sltu_instr  = 0x00b4b3b3L   //    sltu   x7, x9, x11
  val and_instr   = 0x00b4f3b3L   //    and    x7, x9, x11
  val or_instr    = 0x00b4e3b3L   //    or     x7, x9, x11
  val xor_instr   = 0x00b4c3b3L   //    xor    x7, x9, x11
  val sll_instr   = 0x00b493b3L   //    sll    x7, x9, x11
  val srl_instr   = 0x00b4d3b3L   //    srl    x7, x9, x11
  val sub_instr   = 0x40b483b3L   //    sub    x7, x9, x11
  val sra_instr   = 0x40b4d3b3L   //    sra    x7, x9, x11
  val addi_instr  = 0x07b48393L   //    addi   x7, x9, 123
  val slti_instr  = 0x07b4a393L   //    slti   x7, x9, 123
  val sltiu_instr = 0x07b4b393L   //    sltiu  x7, x9, 123
  val andi_instr  = 0x07b4f393L   //    andi   x7, x9, 123
  val ori_instr   = 0x07b4e393L   //    ori    x7, x9, 123
  val xori_instr  = 0x07b4c393L   //    xori   x7, x9, 123
  val slli_instr  = 0x00c49393L   //    slli   x7, x9, 12
  val srli_instr  = 0x00c4d393L   //    srli   x7, x9, 12
  val srai_instr  = 0x40c4d393L   //    srai   x7, x9, 12

  val beq_instr  = 0xff310fe3L  // beq  x2, x3,    -2
  val bne_instr  = 0x7f419fe3L  // bne  x3, x4,  4094
  val blt_instr  = 0x80524063L  // blt  x4, x5, -4096
  val bltu_instr = 0x0062e163L  // bltu x5, x6,     2
  val bge_instr  = 0x00735063L  // bge  x6, x7,
  val bgeu_instr = 0x0083f063L  // bgeu x7, x8,

  val jal_instr1  = 0x7ffff4efL  // jal   x9,       1048574
  val jal_instr2  = 0x800004efL  // jal   x9,      -1048576
  val jalr_instr1 = 0x7ff58567L  // jalr  x10, x11,    2046
  val jalr_instr2 = 0x800605e7L  // jalr  x11, x12,   -2048

  val auipc_instr1 = 0xfffff297L  // auipc x5, 1048575
  val auipc_instr2 = 0x00000317L  // auipc x6, 0x0
  val lui_instr1   = 0xfffff3b7L  // lui   x7, 1048575
  val lui_instr2   = 0x00000437L  // lui   x8, 0x0

  // All instructions are valid
  poke(c.io.fch_dec.instr_valid, 1)

  // Test immediate sign extend range
  testFunc(add_instr1, ALU_ADD_VAL, 3, true, 2047)
  testFunc(add_instr2, ALU_ADD_VAL, 4, true, -2048)

  // Test Alu Function
  testFunc(add_instr,    ALU_ADD_VAL,  7)
  testFunc(slt_instr,    ALU_SLT_VAL,  7)
  testFunc(sltu_instr,   ALU_SLTU_VAL, 7)
  testFunc(and_instr,    ALU_AND_VAL,  7)
  testFunc(or_instr,     ALU_OR_VAL,   7)
  testFunc(xor_instr,    ALU_XOR_VAL,  7)
  testFunc(sll_instr,    ALU_SLL_VAL,  7)
  testFunc(srl_instr,    ALU_SRL_VAL,  7)
  testFunc(sub_instr,    ALU_SUB_VAL,  7)
  testFunc(sra_instr,    ALU_SRA_VAL,  7)
  testFunc(addi_instr,   ALU_ADD_VAL,  7, true, 123)
  testFunc(slti_instr,   ALU_SLT_VAL,  7, true, 123)
  testFunc(sltiu_instr,  ALU_SLTU_VAL, 7, true, 123)
  testFunc(andi_instr,   ALU_AND_VAL,  7, true, 123)
  testFunc(ori_instr,    ALU_OR_VAL,   7, true, 123)
  testFunc(xori_instr,   ALU_XOR_VAL,  7, true, 123)
  testFunc(slli_instr,   ALU_SLL_VAL,  7, true, 12)
  testFunc(srli_instr,   ALU_SRL_VAL,  7, true, 12)
  testFunc(srai_instr,   ALU_SRA_VAL,  7, true, 12)

  testBranch(beq_instr,  BRANCH_BEQ_VAL,     -2)
  testBranch(bne_instr,  BRANCH_BNE_VAL,   4094)
  testBranch(blt_instr,  BRANCH_BLT_VAL,  -4096)
  testBranch(bltu_instr, BRANCH_BLTU_VAL,     2)
  testBranch(bge_instr,  BRANCH_BGE_VAL,      0)
  testBranch(bgeu_instr, BRANCH_BGEU_VAL,     0)

  testJump(jal_instr1,  1048574)
  testJump(jal_instr2, -1048576)
  testJump(jalr_instr1,    2047, true, 10)
  testJump(jalr_instr2,   -2048, true, 11)

  testUpper(auipc_instr1, 0xfffff000, true)
  testUpper(auipc_instr2,          0, true)
  testUpper(lui_instr1,   0xfffff000, false)
  testUpper(lui_instr2,            0, false)

  testStall()
}
