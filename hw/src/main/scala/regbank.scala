package TurboRav

import Chisel._

/** A Register Bank for a RISC-V processor with 32 registers of 32
  * bits each for a total of 1024 bits.
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

  io.rs1_data :=
  Mux(io.reads.rs1.bits === UInt(0),                              UInt(0, Config.xlen),
  Mux(io.reads.rs1.bits === io.write.bits.addr && io.write.valid, io.write.bits.data,
                                                                  regs(io.reads.rs1.bits)))

  io.rs2_data :=
  Mux(io.reads.rs2.bits === UInt(0),                              UInt(0, Config.xlen),
  Mux(io.reads.rs2.bits === io.write.bits.addr && io.write.valid, io.write.bits.data,
                                                                  regs(io.reads.rs2.bits)))
}
