#include <stdbool.h>
#include <stdint.h>
#include "tr.h"

// Returns the button input on button release, doesn't do debouncing.
uint32_t read_input(void)
{
    uint32_t opcode     = 0;
    uint32_t new_opcode = 0;
    bool was_high       = false;
    do {
        opcode = new_opcode;
        new_opcode = *GPIO;
        was_high |= new_opcode != 0;
    } while(! (was_high && new_opcode == 0));
    return opcode;
}

uint32_t calc_new_num(uint32_t num, uint32_t opcode)
{
    switch(opcode){
    case 1:  return num + 1;
    case 2:  return num - 1;
    case 4:  return num * 2;
    case 8:  return num / 2;
    default: return num;
    }
}

int main(void) {
    uint32_t num = 0;
    while(true){
        uint32_t opcode = read_input();  // Polls input.
        num = calc_new_num(num, opcode); // Pure function
        *GPIO = num;
    }
}
