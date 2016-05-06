#include <stdbool.h>
#include "tr.h"

bool board_btn_2_get(void){
    return GPIO0->input[0];
}

bool board_led_1_get(void){
    return GPIO0->output[1];
}
bool board_led_2_get(void){
    return GPIO0->output[2];
}

void board_led_1_set(bool high){
    GPIO0->output[1] = high;
}

void board_led_2_set(bool high){
    GPIO0->output[2] = high;
}
