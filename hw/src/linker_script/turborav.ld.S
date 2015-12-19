/*

    A linker script that fits Turborav's weird memory layout
    requirements. See MemoryMapUtil.scala for details about the memory
    layout.

*/

MEMORY
{
    // The ROM LENGTH dynamically increases to fit the elf program
	// given in rom.scala.
    ROM : ORIGIN = 0x00000000, LENGTH = 256K

    // The RAM LENGTH is hardcoded in Config.scala also.
    RAM : ORIGIN = 0x10000000, LENGTH = 4k
}

REGION_ALIAS("REGION_TEXT"  , ROM);
REGION_ALIAS("REGION_RODATA", ROM);

REGION_ALIAS("REGION_DATA"  , RAM);
REGION_ALIAS("REGION_BSS"   , RAM);

ENTRY( _crt0 )

SECTIONS
{
    .text :
    {
        */crt0.o(.custom_startup_file_section)
        *(.text.startup)
        *(.text)
        *(.text.*)
    } > REGION_TEXT

    .rodata :
    {
        *(.rodata)
        *(.rodata.*)
	    *(.srodata.*)
        rodata_end = .;
    } > REGION_RODATA

    .eh_frame   : { *(.eh_frame   ) } > ROM
    .jcr        : { *(.jcr        ) } > ROM
    .fini_array : { *(.fini_array ) } > ROM
    .init_array : { *(.init_array ) } > ROM

    rom_data_end = .;

    /* Writable small data segment */
	.sdata : AT (rom_data_end)
	{
        initialized_data_start = .;
        *(.sdata)
	    *(.sdata.*)
        sdata_end = .;
	} > RAM

    .data : AT ( ADDR (.init_array) + SIZEOF (.sdata) + SIZEOF (.init_array) )
    {
        *(.data)
        initialized_data_end = .;
    } > REGION_DATA

    initialized_data_size = SIZEOF(.data) + SIZEOF(.sdata);
    initialized_data_load_start = LOADADDR(.sdata);

    .bss :
    {
        _fbss = .;
        *(.bss)
    } > REGION_BSS

    _end = . ;

    // Calculate what the last address of the RAM is so we can use it
    // as a stack pointer.
    ram_end = ORIGIN(RAM) + LENGTH(RAM) ;
}
