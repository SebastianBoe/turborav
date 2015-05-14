package TurboRav

import Chisel._
import Constants._

import Array._
import java.math.BigInteger;

class RavVTest(c: RavV) extends Tester(c) {

  def parseRomContents():Array[BigInteger] = {
    val path = "generated/startup_program.hex"
    val source = scala.io.Source.fromFile(path)
    val lines = source.mkString
    source.close()
    return lines.split("\\n").map(new BigInteger(_, 16))
  }
  val program = parseRomContents()
  for(instruction <- program){
    poke(c.io.response.bits.word, instruction)
    poke(c.io.response.valid, 1)
    step(1)
  }

}
