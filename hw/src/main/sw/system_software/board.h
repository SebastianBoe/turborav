#pragma once

#include <stdbool.h>

// The board module extracts away some board-specific details so SW
// can be written to be portable across dev-kits

bool board_btn_2_get(void);

bool board_led_1_get(void);
bool board_led_2_get(void);

void board_led_1_set(bool);
void board_led_2_set(bool);
