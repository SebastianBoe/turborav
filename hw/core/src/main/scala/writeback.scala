package TurboRav

import Chisel._
import Common._
import Constants._

class Writeback(implicit conf: TurboravConfig) extends Module {

  val io = new WritebackIO()

}