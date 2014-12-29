package TurboRav

import Chisel._
import Constants._

class RavVTest(c: RavV) extends Tester(c) {

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

  val addi_instr_x9  = 0x07b00493l  // li      x9, 123
  val addi_instr_x11 = 0x02a00593l  // li      x11, 42
  val nop            = 0x00000013   // nop

  // I broke this test with the new memory interface. Not sure what to
  // do about that.

  poke(c.dec.io.fch_dec.instr, addi_instr_x9)
  step(1)
  expect(c.dec.io.dec_exe.rd_addr, 9)
  expect(c.dec.io.dec_exe.wrb_ctrl.rd_wen, 1)
  poke(c.dec.io.fch_dec.instr, addi_instr_x11)
  step(1)
  expect(c.exe.io.exe_mem.alu_result, 123)
  poke(c.dec.io.fch_dec.instr, nop)
  step(3)
  poke(c.dec.io.fch_dec.instr, add_instr)
  step(2)
  expect(c.exe.io.exe_mem.alu_result, 165)

}
