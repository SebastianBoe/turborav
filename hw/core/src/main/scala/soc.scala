package TurboRav

import Chisel._
import Common._
import Constants._

// The System-on-Chip module.

// The SoC module instantiates and connects with an apb bus the
// processor to the APB peripherals. It is the toplevel module.

// See MemoryMapUtil for how the memory map is laid out.

class Soc(num_pin_inputs: Int, num_pin_outputs: Int) extends Module {
  val io = new Bundle {
    val pin_inputs  = UInt(INPUT , width = num_pin_inputs )
    val pin_outputs = UInt(OUTPUT, width = num_pin_outputs)
  }

  val ravv    = Module(new RavV())
  val gpio    = Module(new Gpio(num_pin_inputs, num_pin_outputs))

  // Connect the peripherals to the SoC pins
  gpio.io.pin_inputs := io.pin_inputs
  io.pin_outputs := gpio.io.pin_outputs

  // CONNECT the bus master
  ravv.io <> gpio.io.rr
}
