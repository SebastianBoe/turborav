// Copyright (C) 2015 Sebastian Bøe, Joakim Andersson
// License: BSD 2-Clause (see LICENSE for details)

package TurboRav

import Chisel._

class RequestResponseIo extends Bundle {
  val request = new ValidIO( new Bundle {
    val addr    = UInt(width = Config.xlen)
    val wdata   = UInt(width = Config.xlen)
    val byte_en = UInt(width = 2)
    val write   = Bool()
  })

  val response = new ValidIO(new Bundle {
    val word  = UInt(width = Config.xlen)
  }).flip()

  def kill() {
    request.bits.addr    := UInt(0)
    request.bits.wdata   := UInt(0)
    request.bits.byte_en := UInt(0)
    request.bits.write   := Bool(false)
    request.valid        := Bool(false)
  }
}
