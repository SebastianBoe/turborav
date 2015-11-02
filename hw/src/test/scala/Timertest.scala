package TurboRav

import Chisel._
import Constants._

class TimerTest(timer: Timer) extends JUnitTester(timer) {

  // Running timer without setting in_start to '1' results in no update
  expect(timer.io.out_val, BigInt(0))
  step(1000)
  expect(timer.io.out_val, BigInt(0))

  // Set in_start and expect timer to be running
  poke(timer.io.in_start, BigInt(1))
  step(1000)
  expect(timer.io.out_val, BigInt(1000))

  // Set in_reset and expect timer value to be 0
  poke(timer.io.in_reset, BigInt(1))
  step(1)
  expect(timer.io.out_val,BigInt(0) )
  step(1)
  expect(timer.io.out_val, BigInt(0))

  // Unset in_reset and expect timer value to be updated again
  poke(timer.io.in_reset, BigInt(0))
  step(1)
  expect(timer.io.out_val, BigInt(1))
  step(1)
  expect(timer.io.out_val, BigInt(2))

  // Unset in_start and expect timer value to not be updated
  poke(timer.io.in_start, BigInt(0))
  step(1)
  expect(timer.io.out_val, BigInt(2))
}
