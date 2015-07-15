package TurboRav

import Chisel._

class RegBank() extends Module {

  require(isPow2(Config.xlen))

  val io = new Bundle(){
    val rs1_addr = UInt(INPUT, 5)
    val rs2_addr = UInt(INPUT, 5)
    val rd_addr  = UInt(INPUT, 5)
    val rd_wen   = Bool(INPUT)
    val rd_data  = UInt(INPUT, Config.xlen)

    val rs1_data = UInt(OUTPUT, Config.xlen)
    val rs2_data = UInt(OUTPUT, Config.xlen)
  }

  /* Chisel does not support initializing memory */
  val regs = Mem(UInt(width = Config.xlen), 32)

  when (io.rd_wen && io.rd_addr != UInt(0)) {
    regs(io.rd_addr) := io.rd_data
  }

  io.rs1_data :=
  Mux(io.rs1_addr === UInt(0),                 UInt(0, Config.xlen),
  Mux(io.rs1_addr === io.rd_addr && io.rd_wen, io.rd_data,
                                               regs(io.rs1_addr)
                                               ))
  io.rs2_data :=
  Mux(io.rs2_addr === UInt(0),                 UInt(0, Config.xlen),
  Mux(io.rs2_addr === io.rd_addr && io.rd_wen, io.rd_data,
                                               regs(io.rs2_addr)
                                               ))
}