#include <stdint.h>
#include <stdio.h>
#include <string.h>
#include "board.h"
#include "unit.h"

char msg[64];

void main(void) {
    siprintf(msg, "Hello %s", "World!\n");

    write(0, msg, strlen(msg));

    u_pass();
}
