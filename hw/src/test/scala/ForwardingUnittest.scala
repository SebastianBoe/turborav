package TurboRav

import Constants._
import Chisel._

class ForwardingUnitTest(c: ForwardingUnit) extends Tester(c) {

  def test(rs1: Int, rs2: Int, mem_rd: Int, wrb_rd: Int,
           expect_rs1: Int, expect_rs2: Int) {
    poke(c.io.fwu_exe.rs1_addr, rs1)
    poke(c.io.fwu_exe.rs2_addr, rs2)
    poke(c.io.fwu_mem.rd_addr, mem_rd)
    poke(c.io.fwu_wrb.rd_addr, wrb_rd)
    expect(c.io.fwu_exe.rs1_sel, expect_rs1)
    expect(c.io.fwu_exe.rs2_sel, expect_rs2)
  }

  /* Test forwarding with RD write enable */
  poke(c.io.fwu_mem.rd_wen, 1)
  poke(c.io.fwu_wrb.rd_wen, 1)

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
  /* Forward both rs1 and rs2 from memory before writeback stage */
  test( 1, 1,
        1, 1,
        RS_SEL_MEM_VAL, RS_SEL_MEM_VAL)
  /* Dont forward write to register zero */
  test( 0, 0,
        0, 0,
        RS_SEL_DEC_VAL, RS_SEL_DEC_VAL)

  /* Test forwarding without RD write enable */
  poke(c.io.fwu_mem.rd_wen, 0)
  poke(c.io.fwu_wrb.rd_wen, 0)

  /* No forwarding */
  test( 1, 1,
        1, 1,
        RS_SEL_DEC_VAL, RS_SEL_DEC_VAL)

  /* Only writeback stage is writing */
  poke(c.io.fwu_mem.rd_wen, 0)
  poke(c.io.fwu_wrb.rd_wen, 1)

  /* Forward from writeback stage */
  test( 1, 1,
        1, 1,
        RS_SEL_WRB_VAL, RS_SEL_WRB_VAL)

  /* Only memory stage is writing */
  poke(c.io.fwu_mem.rd_wen, 1)
  poke(c.io.fwu_wrb.rd_wen, 0)

  /* Forward from memory stage */
  test( 1, 1,
        1, 1,
        RS_SEL_MEM_VAL, RS_SEL_MEM_VAL)

}
