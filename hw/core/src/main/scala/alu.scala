package TurboRav

import Chisel._

class Alu (val xlen: Int) extends Module with Constants {

    val io = new Bundle {
        val func = UInt(INPUT, 4)
        val inA  = UInt(INPUT, xlen)
        val inB  = UInt(INPUT, xlen)
        val out  = UInt(OUTPUT, xlen)
    }

    val shamt = UInt(width = log2Up(xlen))
    shamt := io.inB(log2Up(xlen)-1, 0)

    val res = Mux( io.func === ALU_ADD, io.inA + io.inB,
              Mux( io.func === ALU_SUB, io.inA - io.inB,
              Mux( io.func === ALU_SLT, io.inA.toSInt() < io.inB.toSInt(),
              Mux( io.func === ALU_SLTU, io.inA < io.inB,
              Mux( io.func === ALU_AND, io.inA & io.inB,
              Mux( io.func === ALU_OR,  io.inA | io.inB,
              Mux( io.func === ALU_XOR, io.inA ^ io.inB,
              Mux( io.func === ALU_SLL, io.inA << shamt,
              Mux( io.func === ALU_SRL, io.inA >> shamt,
              Mux( io.func === ALU_SRA, io.inA.toSInt() >> shamt,
                                        UInt(0)
              )))))))))) // lisp?

  io.out := res
}
