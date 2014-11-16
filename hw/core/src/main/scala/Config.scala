package Common

object Config {
  val xlen     = 32
  val apb_addr_len = 32
  val apb_data_len = 32
  val cache = new CacheConfig()
}

class CacheConfig {
  val cacheLineWidth = 128
  val numEntries     = 128
  val associativity  = 1
}
