#include <stdbool.h>
#include "tr.h"

bool board_get_btn2(void){
    return GPIO0->input[0];
}

bool board_get_led1(void){
    return GPIO0->output[1];
}
bool board_get_led2(void){
    return GPIO0->output[2];
}

void board_set_led1(bool high){
    GPIO0->output[1] = high;
}

void board_set_led2(bool high){
    GPIO0->output[2] = high;
}
