#include <stdint.h>
#include <stdbool.h>
#include "tr.h"

/**
   A smoke test that first toggle LED 1 to signal that the FW is
   alive, and then continuously drives LED 1 with the value of BTN 2.

   See synth/yosys/icoboard/main.pcf
 */

int main(void) {
     // Toggle LED 1 to signal that we have entered main.
    GPIO0->output[1] = 1;
    GPIO0->output[1] = 0;

    while(true) {
        // Icoboard's btn2 is connected to input pin 0. See main.pcf.
        uint32_t btn2 = GPIO0->input[0];
        GPIO0->output[1] = btn2;
    }
}
