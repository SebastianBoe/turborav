// Copyright (C) 2015 Sebastian Bøe, Joakim Andersson
// License: BSD 2-Clause (see LICENSE for details)

package TurboRav

// Contains configuaration constants. This looks like a code smell, we
// should be passing this values around in constructors i believe.

object Config {
  val xlen              = 32
  val apb_addr_len      = 16
  val apb_data_len      = 32
  val cache             = new CacheConfig()

  // NB: The RAM size is also hardcoded in turborav.ld.S. If you
  // change the RAM size here, you must also change turborav.ld.S for
  // the riscv tests and the C-programs to work.
  val ram_size_in_bytes = 4 * 1024
}

class CacheConfig {
  val cache_line_width = 128
  val num_entries      = 128
  val associativity    = 2
}
