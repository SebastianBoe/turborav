#include <stdint.h>
#include <stdbool.h>

/**
   A smoke test that drives LED 1 with the value of BTN 2.

   See synth/yosys/icoboard/main.pcf
 */

#define GPIO ((volatile uint32_t *)0x20060000)

int main(void) {
    while(true) {
        uint32_t btn2 = *GPIO & 1;
        *GPIO = (btn2 << 1);
    }
}
