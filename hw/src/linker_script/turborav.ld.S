/*

    A linker script that fits Turborav's weird memory layout
    requirements. See MemoryMapUtil.scala for details about the memory
    layout.

*/

MEMORY
{
    RAM : ORIGIN = 0x10000000, LENGTH = 256M
    // The ROM LENGTH dynamically increases to fit the elf program
	// given in rom.scala.
    ROM : ORIGIN = 0x00000000, LENGTH = 256K

}

REGION_ALIAS("REGION_TEXT"  , ROM);
REGION_ALIAS("REGION_RODATA", ROM);

REGION_ALIAS("REGION_DATA"  , RAM);
REGION_ALIAS("REGION_BSS"   , RAM);

SECTIONS
{
    .text :
    {
        *(.text.startup)
        *(.text)
    } > REGION_TEXT
    .rodata :
    {
        *(.rodata)
        rodata_end = .;
    } > REGION_RODATA
    .data : AT (rodata_end)
    {
        data_start = .;
        *(.data)
        data_end = .;
    } > REGION_DATA
    data_size = SIZEOF(.data);
    data_load_start = LOADADDR(.data);
    .bss :
    {
        *(.bss)
    } > REGION_BSS
}
