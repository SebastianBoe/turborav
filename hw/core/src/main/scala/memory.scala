package TurboRav

import Chisel._
import Common._
import Constants._

class Memory() extends Module {

  val io = new MemoryIO()

  val exe_mem = Reg(init = new ExecuteMemory())
  when(!io.stall){
    exe_mem := io.exe_mem
  }

  io.mem_wrb <> exe_mem

 }