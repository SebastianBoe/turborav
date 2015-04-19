
### Git workflow
Workflow is up to the programmer.

#### Merge commit
Merge commits should be avoided when there are no conflicts.

### Code style
Unless stated otherwise one must follow the style guide of the

* No trailing-whitespace.
* No tabs.
* Max line length is 80 chars.

### Chisel

* Functions should not contain state
* Modules may be stateless

####A chisel source file should follow the structure of the below

```scala
package Turborav

import Chisel._

// Some high-level description of what the module does.

class MyModule() extends Module {

  // start with io.
 val io = new Bundle {
   val in  = Bool(INPUT)
   val out = Bool(OUTPUT)
 }

  // Instantiation assertions
  require(Array(32, 64) contains conf.xlen)

  //Chisel code.

  io.out := false
}
```