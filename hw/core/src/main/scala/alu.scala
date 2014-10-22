package TurboRav

import Chisel._

// The purely combinatorial Arithmetic Logic Unit.

class Alu (val xlen: Int) extends Module with Constants {

  val io = new Bundle {
    val func = UInt(INPUT, 4)
    val inA  = UInt(INPUT, xlen)
    val inB  = UInt(INPUT, xlen)
    val out  = UInt(OUTPUT, xlen)
  }
  // The value given when no valid func is applied.
  val defaultAluOutput = UInt(0, xlen)

  val shamt = UInt(
    io.inB(log2Up(xlen) - 1, 0),
    width = log2Up(xlen)
  )

  io.out := Lookup(
    io.func,
    defaultAluOutput,
    Array(
      ALU_ADD  -> (io.inA + io.inB),
      ALU_SUB  -> (io.inA - io.inB),
      ALU_SLT  -> (io.inA.toSInt() < io.inB.toSInt()),
      ALU_SLTU -> (io.inA < io.inB),
      ALU_AND  -> (io.inA & io.inB),
      ALU_OR   -> (io.inA | io.inB),
      ALU_XOR  -> (io.inA ^ io.inB),
      ALU_SLL  -> (io.inA << shamt),
      ALU_SRL  -> (io.inA >> shamt),
      ALU_SRA  -> (io.inA.toSInt() >> shamt)
    )
  )
}

// Since I can't find documentation for Lookup I will document it by
// example here.

// Lookup implements the common verilog pattern;

// (Taken from the OpenCores processor Amber25)
// assign instruction      =         instruction_sel == 2'd0 ? fetch_instruction_r       :
//                                   instruction_sel == 2'd1 ? saved_current_instruction :
//                                   instruction_sel == 2'd3 ? hold_instruction          :
//                                                             pre_fetch_instruction     ;

// The Lookup equivalent will look like;
// instruction = Lookup(
//   instruction_sel,
//   pre_fetch_instruction,
//   Array(
//     UInt(0) -> fetch_instruction_r,
//     UInt(1) -> saved_current_instruction_r,
//     UInt(3) -> hold_instruction
//   )
// )

