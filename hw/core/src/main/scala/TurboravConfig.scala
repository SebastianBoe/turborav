package Common
{
  case class TurboravConfig
  {
    val xlen     = 32
    val apb_addr_len = 32
    val apb_data_len = 32
    val rom_contents_path = "resources/rom_contents.hex"
  }
}
