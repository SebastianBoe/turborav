// Copyright (C) 2015 Sebastian Bøe, Joakim Andersson
// License: BSD 2-Clause (see LICENSE for details)

package TurboRav

import Constants._
import Chisel._

// Purely combinatorial Forwarding Unit
class ForwardingUnit extends Module {

  val io = new ForwardingUnitIO()

  io.fwu_exe.rs1_sel := MuxCase(
    RS_SEL_DEC,
    Array(
      All(
        io.fwu_mem.rd_wen,
        io.fwu_mem.rd_addr =/= UInt(0),
        io.fwu_mem.rd_addr === io.fwu_exe.rs1_addr
        ) -> RS_SEL_MEM,
      All(
        io.fwu_wrb.rd_wen,
        io.fwu_wrb.rd_addr =/= UInt(0),
        io.fwu_wrb.rd_addr === io.fwu_exe.rs1_addr
      ) -> RS_SEL_WRB
    )
  )

  io.fwu_exe.rs2_sel := MuxCase(
    RS_SEL_DEC,
    Array(
      All(
        io.fwu_mem.rd_wen,
        io.fwu_mem.rd_addr =/= UInt(0),
        io.fwu_mem.rd_addr === io.fwu_exe.rs2_addr
      ) -> RS_SEL_MEM,
      All(
        io.fwu_wrb.rd_wen,
        io.fwu_wrb.rd_addr =/= UInt(0),
        io.fwu_wrb.rd_addr === io.fwu_exe.rs2_addr
      ) -> RS_SEL_WRB
    )
  )
}
