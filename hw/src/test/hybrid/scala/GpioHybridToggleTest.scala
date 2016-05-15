package TurboRav

import Chisel._
import scala.math.BigInt

class GpioHybridToggleTest(c: Soc, test_name: String)
extends BoardTester(c, isTrace = true) {
  for (i <- 1 to 5) {
    // Wait for SW to drive LED 1 high
    while(! getLed1Status() && within_timeout())
    {
      step(1)
    }

    // Wait for SW to drive LED 1 low
    while(getLed1Status() && within_timeout())
    {
      step(1)
    }
  }

  expect(within_timeout(), "")

  // TODO: Find a re-usable solution to this problem
  private def within_timeout(): Boolean = t < 10000
}
