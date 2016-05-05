#include <stdint.h>

// Magic number found through experimentation
const uint32_t MAGIC = 1000;

void delay_ms(uint32_t ms){
    for(volatile uint32_t i = 0; i < ms * MAGIC; i++);
}
