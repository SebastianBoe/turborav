package TurboRav

import Chisel._
import Constants._

// The System-on-Chip module.

// The SoC module instantiates and connects with an apb bus the
// processor to the APB peripherals. It is the toplevel module.

// See MemoryMapUtil for how the memory map is laid out.

class Soc(
  elf_path: String,
  num_pin_inputs: Int,
  num_pin_outputs: Int
) extends Module {

  val io = new Bundle {
    val pin_inputs  = UInt(INPUT , width = num_pin_inputs )
    val pin_outputs = UInt(OUTPUT, width = num_pin_outputs)

    val dvi      = new DviIo()
  }

  val apbController = Module(new ApbController())

  val ravv    = Module(new RavV(elf_path))

  val gpio    = Module(new Gpio(num_pin_inputs, num_pin_outputs))
  val dviPeri = Module(new DviPeri())

  val apb_slaves = List(
    gpio
    //dviPeri, //TODO
    //timer, //TODO
    //spi, //TODO
  )

  // Connect the peripherals to the SoC pins
  gpio.io.pin_inputs := io.pin_inputs
  io.pin_outputs := gpio.io.pin_outputs
  io.dvi := dviPeri.io.dvi

  // Connect the APB master to it's APB controller
  apbController.io.rr <> ravv.io

  // Connect the APB slaves to the APB bus.
  for ((slave, i) <- apb_slaves.zipWithIndex) {
    slave.io.apb_slave.sel := apbController.io.selx(i)
    slave.io.apb_slave.in  := apbController.io.in
  }

  apbController.io.out.rdata := PriorityMux(
    sel = apbController.io.selx,
    in  = apb_slaves map (_.io.apb_slave.out.rdata)
  )
  apbController.io.out.ready := PriorityMux(
    sel = apbController.io.selx,
    in  = apb_slaves map (_.io.apb_slave.out.ready)
  )

  // TODO: Remove the above redundancy by figuring out how to do this
  // on a Bundle. Why doesn't the below work?

  // apbController.io.out := PriorityMux(
  //   sel = apbController.io.selx,
  //   in  = apb_slaves map (_.io.apb_slave.out)
  // )
}
