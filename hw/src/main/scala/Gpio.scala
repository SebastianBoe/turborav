package TurboRav

import Chisel._
import Constants._

class Gpio(num_pin_inputs: Int, num_pin_outputs: Int) extends Module {
  val io = new Bundle {
    val apb_slave = new ApbSlaveIo()
    val pin_inputs  = UInt(INPUT,  width = num_pin_inputs)
    val pin_outputs = UInt(OUTPUT, width = num_pin_outputs)
  }
  val input_reg  = Reg(init = UInt(0), next = io.pin_inputs)
  val output_reg = Reg(init = UInt(0))

  val s_idle :: s_setup :: Nil = Enum(UInt(), 2)
  val state = Reg(init = s_idle)

  io.apb_slave.out.ready := Bool(false)

  when(state === s_idle) {
    when(io.apb_slave.sel) {
      state := s_setup
    }
  }.elsewhen(state === s_setup) {
    state := s_idle
    io.apb_slave.out.ready := Bool(true)
    when(io.apb_slave.in.write) {
      output_reg := io.apb_slave.in.wdata
    }
  }

  io.pin_outputs := output_reg
  io.apb_slave.out.rdata := input_reg
}
