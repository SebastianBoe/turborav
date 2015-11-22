// Copyright (C) 2015 Sebastian Bøe, Joakim Andersson
// License: BSD 2-Clause (see LICENSE for details)

package TurboRav

// This package contains the constructs needed to connect to an APB
// bus. See ARM's documentation of APB for the signal meanings and
// protocol spec.

import Chisel._

class ApbSlaveIo extends Bundle {
  // Sel is not in InputIo because that simplifies Soc.
  val sel =  Bool(INPUT)
  val in  = new ApbSlaveInputIo()
  val out = new ApbSlaveOutputIo()
}

class ApbSlaveInputIo extends Bundle {
  val addr  =  UInt(INPUT, Config.apb_addr_len)
  val wdata =  UInt(INPUT, Config.apb_data_len)
  val write =  Bool(INPUT)
  val enable = Bool(INPUT)
}

class ApbSlaveOutputIo extends Bundle {
  val rdata  = UInt(OUTPUT, Config.apb_data_len)
  val ready  = Bool(OUTPUT)
}

/**
  APB master controller.
*/
class ApbController extends Module {
  val io = new Bundle {
    val rr = new RequestResponseIo().flip()
    val selx = Bits(OUTPUT, 16) // 16 peripherals
    val in  = new ApbSlaveInputIo().flip()
    val out = new ApbSlaveOutputIo().flip()
  }

  val s_idle :: s_setup :: s_transfer :: Nil = Enum(UInt(), 3)

  val state    = Reg(init = s_idle)
  val addr     = Reg(init = UInt(0))
  val wdata    = Reg(init = UInt(0))
  val selx     = Reg(init = UInt(0))
  val write    = Reg(init = Bool(false))

  private def setup() = {
    write := io.rr.request.bits.write
    addr  := io.rr.request.bits.addr(15, 0)
    selx  := io.rr.request.bits.addr(27, 16)
    wdata := io.rr.request.bits.wdata
  }

  val initiate = io.rr.request.valid
  val terminate = io.out.ready

  when(state === s_idle && initiate){
    state := s_setup
    setup()
  }

  when(state === s_setup) {
    state := s_transfer
  }

  when (state === s_transfer && terminate){
    when(initiate && selx === io.rr.request.bits.addr(27, 16)){
      state := s_setup
      setup()
    }.otherwise{
      state := s_idle
    }
  }

  /* Output APB controller signals on clock edge */
  when(state === s_idle){
    io.in.addr   := UInt(0)
    io.in.wdata  := UInt(0)
    io.in.enable := Bool(false)
    io.in.write  := Bool(false)
    io.selx      := Bits(0)
  } .otherwise {
    io.in.addr   := addr
    io.in.wdata  := Mux(write, wdata, UInt(0))
    io.in.write  := write
    io.in.enable := state === s_transfer
    io.selx      := UIntToOH(selx)
  }

  val valid = state === s_transfer && terminate
  io.rr.response.valid := valid
  io.rr.response.bits.word := ClearIfDisabled(
    data    = io.out.rdata,
    enabled = !write && valid
  )
}

trait ApbSlave {
  def getApbSlaveIo(): ApbSlaveIo
}
