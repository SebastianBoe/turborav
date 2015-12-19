#include <stdint.h>
#include "unit.h"
#include "timer_test.h"

void test(Timer * timer) {
    uint32_t start_time = timer->val;
    timer->start = 1;
    u_gt(timer->val, start_time);
}
