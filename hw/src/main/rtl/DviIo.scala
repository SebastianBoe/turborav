// Copyright (C) 2015 Sebastian Bøe, Joakim Andersson
// License: BSD 2-Clause (see LICENSE for details)

package TurboRav

import Chisel._

class DviIo extends Bundle {
  val chan = Vec.fill(3) {Bool()}.asOutput()
}
