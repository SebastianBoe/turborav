/*

    A linker script that fits Turborav's weird memory layout
    requirements. See MemoryMapUtil.scala for details about the memory
    layout.

*/

SECTIONS
{
    . = 0x0;
    .text : { *(.text) }
    . = 0x10000000;
    .data : { *(.data) }
}
