// Copyright (C) 2015 Sebastian Bøe, Joakim Andersson
// License: BSD 2-Clause (see LICENSE for details)

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
  num_pin_outputs: Int,
  fpga: Boolean
) extends Module {

  val io = new Bundle {
    val pin_inputs  = UInt(INPUT , width = num_pin_inputs )
    val pin_outputs = UInt(OUTPUT, width = num_pin_outputs)

    val dvi      = new DviIo()
  }

  val apbController = Module(new ApbController())

  val ravv    = Module(new RavV(elf_path, fpga))

  val gpio    = Module(new Gpio(num_pin_inputs, num_pin_outputs))
  val dviPeri = Module(new DviPeri())

  val apb_slaves = (for (i <- 1 to 6) yield Module(new TimerPeri())) ++ List(
    gpio,
    dviPeri
    //spi, //TODO
  )
  // 6 timers, 1 more than nrf52 has! :D

  // Connect the peripherals to the SoC pins
  gpio.io.pin_inputs := io.pin_inputs
  io.pin_outputs := gpio.io.pin_outputs
  io.dvi := dviPeri.io.dvi

  // Connect the APB master to it's APB controller
  apbController.io.rr <> ravv.io

  // Connect the APB slaves to the APB bus.
  for ((slave, i) <- apb_slaves.zipWithIndex) {
    slave.getApbSlaveIo.sel := apbController.io.selx(i)
    slave.getApbSlaveIo.in  := apbController.io.in
  }

  apbController.io.out.rdata := PriorityMux(
    sel = apbController.io.selx,
    in  = apb_slaves map (_.getApbSlaveIo.out.rdata)
  )
  apbController.io.out.ready := PriorityMux(
    sel = apbController.io.selx,
    in  = apb_slaves map (_.getApbSlaveIo.out.ready)
  )

  // TODO: Remove the above redundancy by figuring out how to do this
  // on a Bundle. Why doesn't the below work?

  // apbController.io.out := PriorityMux(
  //   sel = apbController.io.selx,
  //   in  = apb_slaves map (_.io.apb_slave.out)
  // )
}
