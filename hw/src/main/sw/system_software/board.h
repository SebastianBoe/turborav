#pragma once

#include <stdbool.h>

// The board module extracts away some board-specific details so SW
// can be written to be portable across dev-kits

bool board_get_btn2(void);

bool board_get_led1(void);
bool board_get_led2(void);

void board_set_led1(bool);
void board_set_led2(bool);
