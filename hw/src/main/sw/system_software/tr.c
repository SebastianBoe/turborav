#include "tr.h"

Timer * get_timer_instance(int i) {
    return (Timer *)(BASE_TIMER0 + i * 0x1000UL);
}
