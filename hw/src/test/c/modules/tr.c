#include "tr.h"

Timer * get_timer_instance(int i) {
    return (Timer *)(TIMER0_BASE + i * 0x1000UL);
}
