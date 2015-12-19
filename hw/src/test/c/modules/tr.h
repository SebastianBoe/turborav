#include <stdint.h>

// TODO: Generate this from the scala code.

#define NUM_TIMER_INSTANCES 6

#define APB_BASE 0x20000000UL

#define TIMER0_BASE (APB_BASE + 0 * 0x1000UL)
#define TIMER1_BASE (APB_BASE + 1 * 0x1000UL)
#define TIMER2_BASE (APB_BASE + 2 * 0x1000UL)
#define TIMER3_BASE (APB_BASE + 3 * 0x1000UL)
#define TIMER4_BASE (APB_BASE + 4 * 0x1000UL)
#define TIMER5_BASE (APB_BASE + 5 * 0x1000UL)

#define TIMER0 ((Timer *) TIMER0_BASE)

typedef struct {
    volatile uint32_t start;
    volatile uint32_t reset;
    volatile uint32_t val;
} Timer;

Timer * get_timer_instance(int i);
