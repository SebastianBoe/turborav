// Copyright (C) 2015 Sebastian Bøe, Joakim Andersson
// License: BSD 2-Clause (see LICENSE for details)

package TurboRav

import Chisel._

class TimerPeri extends Module with ApbSlave {
  val io = new Bundle {
    val apb_slave = new ApbSlaveIo()
  }
  def getApbSlaveIo = io.apb_slave

  val timer = Module(new Timer())

  val s_idle :: s_setup :: Nil = Enum(UInt(), 2)
  val state = Reg(init = s_idle)

  io.apb_slave.out.ready := Bool(false)
  io.apb_slave.out.rdata := Bool(false)

  timer.io.in_start := Bool(false)
  timer.io.in_reset := Bool(false)

  switch(state) {
    is(s_idle) {
      when(io.apb_slave.sel) {
        state := s_setup
      }
    }
    is(s_setup) {
      state := s_idle
      io.apb_slave.out.ready := Bool(true)
      switch(io.apb_slave.in.addr(3,2)){
        is(UInt(0)) { timer.io.in_start := io.apb_slave.in.wdata(0) }
        is(UInt(1)) { timer.io.in_reset := io.apb_slave.in.wdata(0) }
        is(UInt(2)) { io.apb_slave.out.rdata := timer.io.out_val }
      }
    }
  }
}
