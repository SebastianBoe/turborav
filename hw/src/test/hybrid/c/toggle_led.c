#include <stdint.h>
#include <stdbool.h>
#include "board.h"

int main(void) {
    for(uint32_t i = 0; i < 10; i++){
        // Toggle led 1 10 times
        board_led_1_toggle();
    }
}
