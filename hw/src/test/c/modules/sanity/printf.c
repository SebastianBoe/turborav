#include <stdint.h>
#include "board.h"
#include "unit.h"

void main(void) {
    board_led_1_set(1);
    iprintf("Hello World!");
    board_led_2_set(1);
    
    u_pass();
}
