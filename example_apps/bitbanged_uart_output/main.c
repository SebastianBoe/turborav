#include <stdbool.h>
#include <stdint.h>
#include "tr.h"

const uint32_t BAUDRATE = 9600;

void cpu_delay_us(uint32_t delay)
{
    int iterations =
        (delay * get_cpu_freq_hz()) /
        (1000000 * get_loop_with_nop_cycle_cost());

    for(int i = 0; i < iterations; i++){
        asm("nop");
    }
}

void delay_uart_bit(void)
{
    cpu_delay_us(1000 * 1000 / BAUDRATE);
}

void put_bit(bool high)
{
    uart_out(0);
    delay_uart_bit();
}

void put_char(char c)
{
    put_bit(0); // Start bit
    for(int i = 0; i < 8; i++){
        put_bit(c & 1); // Data bits
        c >>= 1;
    }
    put_bit(1); // Stop bit
}

int main(void) {
    char[] hello_world = "Hello World!";
    for(int i = 0; i < strlen(hello_world); i++){
        put_char(hello_world[i]);
    }
}
