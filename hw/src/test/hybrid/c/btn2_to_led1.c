#include <stdint.h>
#include <stdbool.h>

/**
   A smoke test that drives LED 1 with the value of BTN 2.

   See synth/yosys/icoboard/main.pcf
 */

#define GPIO ((volatile uint32_t *)0x20060000)

int main(void) {
    // Toggle LED 1 to signal that we have entered main.
    *GPIO = (1 << 1);
    *GPIO = (0 << 1);

    while(true) {
        uint32_t btn2 = *GPIO & 1;
        *GPIO = (btn2 << 1);
    }
}
