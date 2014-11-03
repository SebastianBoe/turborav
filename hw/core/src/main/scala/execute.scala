package TurboRav

import Chisel._
import Common._
import Constants._

class Execute() extends Module {

  val io = new ExecuteIO()

  val alu = Module(new Alu())
}