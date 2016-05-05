#pragma once

#include <stdint.h>

// A blocking delay lib that makes no promises about accuracy, but
// tries it's best at being accurate.

void delay_ms(uint32_t ms);
