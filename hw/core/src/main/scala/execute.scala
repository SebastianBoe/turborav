package TurboRav

import Chisel._
import Common._
import Constants._

class Execute(implicit conf: TurboravConfig) extends Module {

  val io = new ExecuteIO()

  val alu = Module(new Alu())
}