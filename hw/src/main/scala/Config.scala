package TurboRav

object Config {
  val xlen              = 32
  val apb_addr_len      = 32
  val apb_data_len      = 32
  val cache             = new CacheConfig()
  val ram_size_in_bytes = 1024
}

class CacheConfig {
  val cache_line_width = 128
  val num_entries      = 128
  val associativity    = 2
}
