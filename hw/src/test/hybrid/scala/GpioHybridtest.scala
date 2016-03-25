package TurboRav

import Chisel._
import scala.math.BigInt

class GpioHybridTest(c: Soc, test_name: String)
extends JUnitTester(c, isTrace = true) {

  expect(! getLed1Status(), "LED should be off on boot")

  // Wait for SW to drive LED 1, signaling that it has entered main.
  while(! getLed1Status())
  {
    step(1)
  }
  // Wait for SW drive LED 1 low again.
  while(getLed1Status())
  {
    step(1)
  }

  println("CPU has entered main")

  println("Driving BTN2 high")
  driveBtn2(true)

  // Expect that eventually the LED will be driven high.
  while(! getLed1Status())
  {
    step(1)
  }
  println("LED1 was driven high")

  for(i <- 0 until 200){
    expect(getLed1Status(), "LED should be held high")

    step(1)
  }

  println("Driving BTN2 low")
  driveBtn2(false)

  // Expect that eventually the LED will be driven low.
  while(getLed1Status())
  {
    step(1)
  }

  println("LED1 was driven low")
  for(i <- 0 until 200){
    expect(! getLed1Status(), "LED should be held low")

    step(1)
  }

  private def driveBtn2(level: Boolean) = {
    poke(c.io.pin_inputs, level)
  }

  private def getLed1Status(): Boolean = {
    (peek(c.io.pin_outputs) & 2) > 0
  }

  override def getTestName: String = {
    test_name + "_" + "GpioHybridtest"
  }
}
