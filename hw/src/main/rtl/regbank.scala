package TurboRav

import Chisel._

/** A Register Bank for a RISC-V processor with 32 registers of 32
  * bits each for a total of 1024 bits.
  *
  * Reads and writes are done synchronously so that FPGA block RAM can
  * be used as the underlying memory technology.
  *
  * Register 0 AKA x0 is treated as special; all reads to it return 0
  */
class RegBank extends Module {
  val io = new Bundle(){
    val reads = new Bundle {
      val rs1 = Valid(UInt(width = 5)).flip()
      val rs2 = Valid(UInt(width = 5)).flip()
    }
    val write = Valid(new RegWrite()).flip()

    val rs1_data = UInt(OUTPUT, Config.xlen)
    val rs2_data = UInt(OUTPUT, Config.xlen)
  }

  val regs = Mem(UInt(width = Config.xlen), 32)

  when (io.write.valid && io.write.bits.addr =/= UInt(0)) {
    regs(io.write.bits.addr) := io.write.bits.data
  }

  val rs1_addr_prev = Reg(init = UInt(0), next = io.reads.rs1.bits)
  val rs2_addr_prev = Reg(init = UInt(0), next = io.reads.rs2.bits)

  // Mux reads of x0 to be 0 because x0 must be hardwired to 0.
  val rs1_read = Mux(rs1_addr_prev === UInt(0), UInt(0), regs(rs1_addr_prev))
  val rs2_read = Mux(rs2_addr_prev === UInt(0), UInt(0), regs(rs2_addr_prev))

  io.rs1_data := rs1_read
  io.rs2_data := rs2_read
}
