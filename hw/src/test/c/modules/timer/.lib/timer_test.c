#include "timer_test.h"
#include "unit.h"

void main(void) {
    for(int i = 0; i < NUM_TIMER_INSTANCES; i++){
        test(get_timer_instance(i));
    }
    u_pass();
}
