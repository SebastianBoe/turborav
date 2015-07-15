package TurboRav

import Chisel._

class DviIo extends Bundle {
  val chan = Vec.fill(3) {Bool()}.asOutput()
}
