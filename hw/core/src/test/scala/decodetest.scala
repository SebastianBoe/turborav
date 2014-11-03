package TurboRav

import Chisel._
import Constants._

class DecodeTest(c: Decode) extends Tester(c) {

  def signed(a: Long) = { (a & 0x00000000ffffffffl) }

  def test_func(instr: Long,
                exp_func: Long,
                is_imm: Boolean = false,
                exp_imm: Long = 0) = {
    poke(c.io.fch_dec.instr, instr)
    step(1)
    expect(c.io.dec_exe.exe_ctrl.alu_func, exp_func)
    expect(c.io.dec_exe.exe_ctrl.alu_in_a_sel, ALU_IN_A_RS1_VAL)
    if (is_imm){
      expect(c.io.dec_exe.imm, signed(exp_imm))
      expect(c.io.dec_exe.exe_ctrl.alu_in_b_sel, ALU_IN_B_IMM_VAL)
    } else {
      expect(c.io.dec_exe.exe_ctrl.alu_in_b_sel, ALU_IN_B_RS2_VAL)
    }
  }

  val  add_instr1 = 0x7ff30193l   //    addi   x3, x6, 2047
  val  add_instr2 = 0x80138213l   //    addi   x4, x7, -2047

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

  // All instructions are valid
  poke(c.io.fch_dec.instr_valid, 1)

  // Test immediate sign extend range
  test_func(add_instr1, ALU_ADD_VAL, true, 2047)
  test_func(add_instr2, ALU_ADD_VAL, true, -2047)

  // Test Alu Function
  test_func(add_instr   ,ALU_ADD_VAL)
  test_func(slt_instr   ,ALU_SLT_VAL)
  test_func(sltu_instr  ,ALU_SLTU_VAL)
  test_func(and_instr   ,ALU_AND_VAL)
  test_func(or_instr    ,ALU_OR_VAL)
  test_func(xor_instr   ,ALU_XOR_VAL)
  test_func(sll_instr   ,ALU_SLL_VAL)
  test_func(srl_instr   ,ALU_SRL_VAL)
  test_func(sub_instr   ,ALU_SUB_VAL)
  test_func(sra_instr   ,ALU_SRA_VAL)
  test_func(addi_instr  ,ALU_ADD_VAL,  true, 123)
  test_func(slti_instr  ,ALU_SLT_VAL,  true, 123)
  test_func(sltiu_instr ,ALU_SLTU_VAL, true, 123)
  test_func(andi_instr  ,ALU_AND_VAL,  true, 123)
  test_func(ori_instr   ,ALU_OR_VAL,   true, 123)
  test_func(xori_instr  ,ALU_XOR_VAL,  true, 123)
  test_func(slli_instr  ,ALU_SLL_VAL,  true, 12)
  test_func(srli_instr  ,ALU_SRL_VAL,  true, 12)
  test_func(srai_instr  ,ALU_SRA_VAL,  true, 12)

}