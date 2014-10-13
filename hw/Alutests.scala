/* Fancy header */

package TurboRav

import Chisel._

class AluTests(c: Alu) extends Tester(c) {
  step(1)
  expect(c.io.out, 42)
}