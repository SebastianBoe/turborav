#include <stdbool.h>
#include <stdint.h>

/**
  This was the first c-program to be run on Turborav. Archived for
  sentimental reasons. It is unlikely to be compatible with later
  HW-revisions.

  The program reads Turborav's input pins and writes the value found
  to the output pins. It could be used e.g. to make buttons turn LED's
  on. The applications are endless really.

  It was compiled as so:

  riscv64-unknown-elf-gcc -m32 -static -nostdlib -ffreestanding
  -Wa,-march=RVIMAFDXhwacha -fvisibility=hidden -Thw/core/src/misc/turborav.ld
  -O3 hello_world.c
 */

#define GPIO (volatile uint32_t *)0x20000000

int main(void) {
    while(true) *GPIO = *GPIO;
}
