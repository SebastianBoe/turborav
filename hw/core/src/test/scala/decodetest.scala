package TurboRav

import Chisel._
import Constants._

class DecodeTest(c: Decode) extends Tester(c) {

  def signed(a: Long) = { (a & 0x00000000ffffffffl) }

  def test_func(instr: Long,
                exp_func: Long,
                rs_addr: Int,
                is_imm: Boolean = false,
                exp_imm: Long = 0) = {
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

  def test_branch(instr: Long,
                  exp_func: Long,
                  exp_imm: Long) = {
    poke(c.io.fch_dec.instr, instr)
    step(1)
    expect(c.io.dec_exe.exe_ctrl.bru_func, exp_func)
    expect(c.io.dec_exe.exe_ctrl.alu_in_a_sel, ALU_IN_A_PC_VAL)
    expect(c.io.dec_exe.wrb_ctrl.rd_wen, 0)
    expect(c.io.dec_exe.imm, signed(exp_imm))
    expect(c.io.dec_exe.exe_ctrl.alu_in_b_sel, ALU_IN_B_IMM_VAL)
  }

  def test_jump(instr: Long,
                exp_imm: Long,
                is_jalr: Boolean = false,
                rd_addr: Int = 0) = {
    poke(c.io.fch_dec.instr, instr)
    step(1)
    expect(c.io.dec_exe.exe_ctrl.bru_func, BRANCH_BJMP_VAL)
    expect(c.io.dec_exe.exe_ctrl.alu_func, ALU_ADD_VAL)
    if(is_jalr){
      expect(c.io.dec_exe.exe_ctrl.alu_in_a_sel, ALU_IN_A_RS1_VAL)
      expect(c.io.dec_exe.rd_addr, rd_addr)
    }
    else
      expect(c.io.dec_exe.exe_ctrl.alu_in_a_sel, ALU_IN_A_PC_VAL)

    expect(c.io.dec_exe.wrb_ctrl.rd_sel, RD_PC_VAL)
    expect(c.io.dec_exe.wrb_ctrl.rd_wen, 1)
    expect(c.io.dec_exe.imm, signed(exp_imm))
    expect(c.io.dec_exe.exe_ctrl.alu_in_b_sel, ALU_IN_B_IMM_VAL)
  }

  def test_upper(instr: Long,
                 exp_imm: Long,
                 with_pc: Boolean){
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

  def write_reg(addr: Int, data: BigInt){
    poke(c.io.wrb_dec.rd_wen, 1)
    poke(c.io.wrb_dec.rd_addr, addr)
    poke(c.io.wrb_dec.rd_data, data)
  }
  def write_reg_disable(){
    poke(c.io.wrb_dec.rd_wen, 0)
  }

  val  add_instr1 = 0x7ff30193l   //    addi   x3, x6, 2047
  val  add_instr2 = 0x80038213l   //    addi   x4, x7, -2048

  val add_instr   = 0x00b483b3l   //    add    x7, x9, x11
  val slt_instr   = 0x00b4a3b3l   //    slt    x7, x9, x11
  val sltu_instr  = 0x00b4b3b3l   //    sltu   x7, x9, x11
  val and_instr   = 0x00b4f3b3l   //    and    x7, x9, x11
  val or_instr    = 0x00b4e3b3l   //    or     x7, x9, x11
  val xor_instr   = 0x00b4c3b3l   //    xor    x7, x9, x11
  val sll_instr   = 0x00b493b3l   //    sll    x7, x9, x11
  val srl_instr   = 0x00b4d3b3l   //    srl    x7, x9, x11
  val sub_instr   = 0x40b483b3l   //    sub    x7, x9, x11
  val sra_instr   = 0x40b4d3b3l   //    sra    x7, x9, x11
  val addi_instr  = 0x07b48393l   //    addi   x7, x9, 123
  val slti_instr  = 0x07b4a393l   //    slti   x7, x9, 123
  val sltiu_instr = 0x07b4b393l   //    sltiu  x7, x9, 123
  val andi_instr  = 0x07b4f393l   //    andi   x7, x9, 123
  val ori_instr   = 0x07b4e393l   //    ori    x7, x9, 123
  val xori_instr  = 0x07b4c393l   //    xori   x7, x9, 123
  val slli_instr  = 0x00c49393l   //    slli   x7, x9, 12
  val srli_instr  = 0x00c4d393l   //    srli   x7, x9, 12
  val srai_instr  = 0x40c4d393l   //    srai   x7, x9, 12

  val beq_instr  = 0xff310fe3l  // beq  x2, x3,    -2
  val bne_instr  = 0x7f419fe3l  // bne  x3, x4,  4094
  val blt_instr  = 0x80524063l  // blt  x4, x5, -4096
  val bltu_instr = 0x0062e163l  // bltu x5, x6,     2
  val bge_instr  = 0x00735063l  // bge  x6, x7,
  val bgeu_instr = 0x0083f063l  // bgeu x7, x8,

  val jal_instr1  = 0x7ffff4efl  // jal   x9,       1048574
  val jal_instr2  = 0x800004efl  // jal   x9,      -1048576
  val jalr_instr1 = 0x7ff58567l  // jalr  x10, x11,    2046
  val jalr_instr2 = 0x800605e7l  // jalr  x11, x12,   -2048

  val auipc_instr1 = 0xfffff297l  // auipc x5, 1048575
  val auipc_instr2 = 0x00000317l  // auipc x6, 0x0
  val lui_instr1   = 0xfffff3b7l  // lui   x7, 1048575
  val lui_instr2   = 0x00000437l  // lui   x8, 0x0

  // All instructions are valid
  poke(c.io.fch_dec.instr_valid, 1)

  // Test immediate sign extend range
  test_func(add_instr1, ALU_ADD_VAL, 3, true, 2047)
  test_func(add_instr2, ALU_ADD_VAL, 4, true, -2048)

  // Test Alu Function
  test_func(add_instr,    ALU_ADD_VAL,  7)
  test_func(slt_instr,    ALU_SLT_VAL,  7)
  test_func(sltu_instr,   ALU_SLTU_VAL, 7)
  test_func(and_instr,    ALU_AND_VAL,  7)
  test_func(or_instr,     ALU_OR_VAL,   7)
  test_func(xor_instr,    ALU_XOR_VAL,  7)
  test_func(sll_instr,    ALU_SLL_VAL,  7)
  test_func(srl_instr,    ALU_SRL_VAL,  7)
  test_func(sub_instr,    ALU_SUB_VAL,  7)
  test_func(sra_instr,    ALU_SRA_VAL,  7)
  test_func(addi_instr,   ALU_ADD_VAL,  7, true, 123)
  test_func(slti_instr,   ALU_SLT_VAL,  7, true, 123)
  test_func(sltiu_instr,  ALU_SLTU_VAL, 7, true, 123)
  test_func(andi_instr,   ALU_AND_VAL,  7, true, 123)
  test_func(ori_instr,    ALU_OR_VAL,   7, true, 123)
  test_func(xori_instr,   ALU_XOR_VAL,  7, true, 123)
  test_func(slli_instr,   ALU_SLL_VAL,  7, true, 12)
  test_func(srli_instr,   ALU_SRL_VAL,  7, true, 12)
  test_func(srai_instr,   ALU_SRA_VAL,  7, true, 12)

  // Test register write and read
  // Make sure that register written by WRB will be read
  poke(c.io.fch_dec.instr, add_instr)
  write_reg(9, 123)
  step(1)
  write_reg_disable()
  expect(c.io.dec_exe.rs1, 123)
  expect(c.io.dec_exe.rs2, 0)

  poke(c.io.fch_dec.instr, add_instr)
  write_reg(11, 123)
  step(1)
  write_reg_disable()
  expect(c.io.dec_exe.rs1, 123)
  expect(c.io.dec_exe.rs2, 123)

  poke(c.io.fch_dec.instr, add_instr)
  write_reg(10, 321)
  step(1)
  write_reg_disable()
  expect(c.io.dec_exe.rs1, 123)
  expect(c.io.dec_exe.rs2, 123)


  test_branch(beq_instr,  BRANCH_BEQ_VAL,     -2)
  test_branch(bne_instr,  BRANCH_BNE_VAL,   4094)
  test_branch(blt_instr,  BRANCH_BLT_VAL,  -4096)
  test_branch(bltu_instr, BRANCH_BLTU_VAL,     2)
  test_branch(bge_instr,  BRANCH_BGE_VAL,      0)
  test_branch(bgeu_instr, BRANCH_BGEU_VAL,     0)

  test_jump(jal_instr1,  1048574)
  test_jump(jal_instr2, -1048576)
  test_jump(jalr_instr1,    2047, true, 10)
  test_jump(jalr_instr2,   -2048, true, 11)

  test_upper(auipc_instr1, 0xfffff000, true)
  test_upper(auipc_instr2,          0, true)
  test_upper(lui_instr1,   0xfffff000, false)
  test_upper(lui_instr2,            0, false)
}