package TurboRav

import Chisel._

class SerializerTest(c: Serializer) extends Tester(c) {

  poke(c.io.cond, 0)
  step(3) // Waiting a couple of cycles will test that the
          // serializer's counter doesn't tick while it is not
          // enabled.

  poke(c.io.in, 6)
  poke(c.io.cond, 1)

  expect(c.io.out, 0); step(1);
  expect(c.io.out, 1); step(1);
  expect(c.io.out, 1); step(1);
  expect(c.io.out, 0); step(1);

}
