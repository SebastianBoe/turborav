#include "unit.h"
#include <string.h>
#include <unistd.h>

void main(void) {
    char * msg = "Hello World!";
    write(0, msg, strlen(msg));
    u_pass();
}
