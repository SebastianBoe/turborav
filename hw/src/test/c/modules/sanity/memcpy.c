#include <stdint.h>
#include <stdio.h>
#include <string.h>
#include "board.h"
#include "unit.h"

char msg[64];

void main(void) {
    size_t len = 7;

    memcpy(msg, "Hello\n", len);

    write(0, msg, len);

    u_pass();
}
