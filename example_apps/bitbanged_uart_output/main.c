#include <stdbool.h>
#include <stdint.h>
#include "tr.h"

const uint32_t BAUDRATE = 9600;

void cpu_delay_us(uint32_t delay)
{
    
}

int main(void) {
    uart_out(0);
    delay_uart_bit();
}
