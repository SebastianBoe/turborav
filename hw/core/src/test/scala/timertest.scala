package TurboRav

import Chisel._
import Constants._

class TimerTest(timer: Timer) extends Tester(timer) {
  val thousand = BigInt(1000)
  val zero = BigInt(0)

  expect(timer.io.out_val, zero)
  step(1000)
  expect(timer.io.out_val, thousand)
}
