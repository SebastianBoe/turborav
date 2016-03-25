#include <stdint.h>
#include <stdbool.h>

// TODO: Generate this from the scala code.
// Relevant discussion for generating this file:
// https://groups.google.com/forum/#!topic/chisel-users/JhCajGBQ4TQ

#define NUM_TIMER_INSTANCES 6

// Actual number of pins is probably not 64, but we reserve space for
// 64 pins in the address space.
// NB: Copied in Gpio.scala.
// TODO: Remove this redundancy
#define MAX_NUM_GPIO_INPUT_PINS 64
#define MAX_NUM_GPIO_OUTPUT_PINS 64

#define BASE_APB 0x20000000UL
#define APB_ADDR_SPACE (0x10000UL)

#define BASE_TIMER0 (BASE_APB)
#define BASE_TIMER1 (BASE_TIMER0 + APB_ADDR_SPACE)
#define BASE_TIMER2 (BASE_TIMER1 + APB_ADDR_SPACE)
#define BASE_TIMER3 (BASE_TIMER2 + APB_ADDR_SPACE)
#define BASE_TIMER4 (BASE_TIMER3 + APB_ADDR_SPACE)
#define BASE_TIMER5 (BASE_TIMER4 + APB_ADDR_SPACE)
#define BASE_TIMER_LAST BASE_TIMER5

#define BASE_GPIO0 (BASE_TIMER_LAST + APB_ADDR_SPACE)

#define TIMER0 ((Timer *) BASE_TIMER0)
#define GPIO0  ((Gpio  *) BASE_GPIO0 )

typedef struct {
    volatile uint32_t start;
    volatile uint32_t reset;
    volatile uint32_t val;
} Timer;

typedef struct {
    volatile uint32_t input[MAX_NUM_GPIO_INPUT_PINS];
    volatile uint32_t output[MAX_NUM_GPIO_OUTPUT_PINS];
} Gpio;

Timer * get_timer_instance(int i);
