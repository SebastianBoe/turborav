// Copyright (C) 2015 Sebastian Bøe, Joakim Andersson
// License: BSD 2-Clause (see LICENSE for details)

package TurboRav

import Chisel._
import Constants._

class Gpio(num_pin_inputs: Int, num_pin_outputs: Int)
    extends Module
    with ApbSlave {

  val io = new Bundle {
    val apb_slave = new ApbSlaveIo()
    val pin_inputs  = UInt(INPUT,  width = num_pin_inputs)
    val pin_outputs = UInt(OUTPUT, width = num_pin_outputs)
  }

  // NB: Copied in tr.h
  // TODO: Define this only one place
  val GPIO_PINS_ADDRESS_SPACE = 64 * 4

  // Used by Soc to dynamically connect slaves
  def getApbSlaveIo = io.apb_slave

  def is_input_pin_addr(addr: UInt): Bool = {
    InRange(addr, 0, GPIO_PINS_ADDRESS_SPACE)
  }

  def is_output_pin_addr(addr: UInt): Bool = {
    InRange(addr, GPIO_PINS_ADDRESS_SPACE, 2 * GPIO_PINS_ADDRESS_SPACE)
  }

  // Convert an APB address to a pin number. Each pin is mapped to
  // the first bit of a 4-byte word, so converting from byte address
  // to word address gives us the pin number.
  def apb_addr_to_bit_index(addr: UInt): UInt = {
    addr >> UInt(2)
  }

  val input_reg  = Reg(init = UInt(0), next = io.pin_inputs)
  val output_reg = Reg(UInt(width = num_pin_outputs))

  val s_idle :: s_setup :: Nil = Enum(UInt(), 2)
  val state = Reg(init = s_idle)

  io.apb_slave.out.ready := Bool(false)
  io.apb_slave.out.rdata := UInt(0)

  // Alias for brevity reasons.
  val addr = io.apb_slave.in.addr

  when(state === s_idle) {
    when(io.apb_slave.sel) {
      state := s_setup
    }
  }.elsewhen(state === s_setup) {
    state := s_idle
    io.apb_slave.out.ready := Bool(true)
    when(io.apb_slave.in.write) {
      when(is_output_pin_addr(addr)) {
        val output_addr = addr - UInt(GPIO_PINS_ADDRESS_SPACE)
        val index = apb_addr_to_bit_index(output_addr)
        val high = io.apb_slave.in.wdata(0) // Only LSbit is used

        printf("Request to write pin %d with value %d\n", index, high)
        printf("index = %d, output_addr = %d, addr = %d\n", index, output_addr, addr)
        assert(index < UInt(num_pin_outputs), "index out of bounds")

        output_reg(index) := high
      }.otherwise {
        assert(Bool(false), "SW wrote to a read-only register")
        printf("At address %d\n", addr)
      }
    }.otherwise {
      // Read request
      val read_reg = MuxCase(
        UInt(0),
        Array(
          is_input_pin_addr(addr) -> input_reg,
          is_output_pin_addr(addr) -> output_reg
        )
      )
      io.apb_slave.out.rdata := read_reg(apb_addr_to_bit_index(addr))
    }
  }

  io.pin_outputs := output_reg
}
