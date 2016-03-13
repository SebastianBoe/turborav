#include <stdint.h>
#include <stdbool.h>

/**
   A smoke test that turns on LED 1 and LED3.

   The first turborav program to run on a completely open source tool
   flow!

   See synth/yosys/icoboard/main.pcf
 */

#define GPIO ((volatile uint32_t *)0x20060000)

int main(void) {
    while(true) {
        // Drive led 1 and 3 high
        *GPIO = (1 << 1) | (1 << 3);
    }
}
