package TurboRav

import Constants._
import Common._
import Chisel._

class ForwardingUnitTest(c: ForwardingUnit) extends Tester(c) {

  def test(rs1: Int, rs2: Int, mem_rd: Int, wrb_rd: Int,
           expect_rs1: Int, expect_rs2: Int) = {
    poke(c.io.fwu_exe.rs1_addr, rs1)
    poke(c.io.fwu_exe.rs2_addr, rs2)
    poke(c.io.fwu_mem.rd_addr, mem_rd)
    poke(c.io.fwu_wrb.rd_addr, wrb_rd)
    expect(c.io.fwu_exe.rs1_sel, expect_rs1)
    expect(c.io.fwu_exe.rs2_sel, expect_rs2)
  }

  /* No forwarding */
  test( 1, 2,
        3, 4,
        RS_SEL_DEC_VAL, RS_SEL_DEC_VAL)
  /* Forward rs1 from memory stage */
  test( 1, 2,
        1, 4,
        RS_SEL_MEM_VAL, RS_SEL_DEC_VAL)
  /* Forward rs2 from memory stage */
  test( 1, 2,
        2, 4,
        RS_SEL_DEC_VAL, RS_SEL_MEM_VAL)
  /* Forward rs1 from writeback stage */
  test( 1, 2,
        3, 1,
        RS_SEL_WRB_VAL, RS_SEL_DEC_VAL)
  /* Forward rs2 from writeback stage */
  test( 1, 2,
        3, 2,
        RS_SEL_DEC_VAL, RS_SEL_WRB_VAL)
  /* Forward rs1 and rs2 from memory stage*/
  test( 1, 1,
        1, 4,
        RS_SEL_MEM_VAL, RS_SEL_MEM_VAL)
  /* Forward rs1 and rs2 from writeback stage */
  test( 2, 2,
        3, 2,
        RS_SEL_WRB_VAL, RS_SEL_WRB_VAL)
  /* Forward rs1 from memory before writeback stage */
  test( 1, 2,
        1, 1,
        RS_SEL_MEM_VAL, RS_SEL_DEC_VAL)
  /* Forward rs2 from memory before writeback stage */
  test( 1, 2,
        2, 2,
        RS_SEL_DEC_VAL, RS_SEL_MEM_VAL)
  /* Forward both rs1 andrs2 from memory before writeback stage */
  test( 1, 1,
        1, 1,
        RS_SEL_MEM_VAL, RS_SEL_MEM_VAL)

}