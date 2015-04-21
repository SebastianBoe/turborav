package TurboRav

import Chisel._
import Apb._
import Common._
import Constants._

class Gpio(num_pin_inputs: Int, num_pin_outputs: Int) extends Module {
  val io = new Bundle {
    val rr      = new RequestResponseIo().flip()
    val pin_inputs  = UInt(INPUT,  width = num_pin_inputs)
    val pin_outputs = UInt(OUTPUT, width = num_pin_outputs)
  }
  val input_reg  = Reg(init = UInt(0), next = io.pin_inputs)
  val output_reg = Reg(init = UInt(0))


  io.rr.response.bits.word := UInt(0)
  when(io.rr.request.valid){
    val request = io.rr.request.bits
    when(request.write){
      output_reg := request.wdata
    }.otherwise {
      io.rr.response.bits.word := input_reg
    }
  }
  io.pin_outputs := output_reg

  // There is no heavy lifting done in this peripheral so we can
  // immediately respond positively to requests.
  io.rr.response.valid := Bool(true)
}
