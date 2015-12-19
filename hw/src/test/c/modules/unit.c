#include <stdint.h>
#include "encoding.h"
#include "unit.h"
#include <stdio.h>


void pad_with_nops(void)
{
    // The simulator peeks at the instructions being passed from fetch
    // to decode. If it finds an instruction like this it will do
    // something interesting. We pad with nops so that if the CPU
    // mispredicts a branch and starts executing the code here it will
    // only find nops before it catches it's misprediction.
    asm volatile ("nop");
    asm volatile ("nop");
    asm volatile ("nop");
    asm volatile ("nop");
    asm volatile ("nop");
    asm volatile ("nop");
}

void u_gt(uint32_t a, uint32_t b){
    if (! (a > b)){
        iprintf("ERROR: %u > %u", a, b);
        u_fail();
    }
}

void u_pass(void) {
    // NB: Has to be this exact instruction

    pad_with_nops();
    asm volatile ("csrwi	tohost, 1");

 infinite_loop:
    goto infinite_loop;
}

void u_fail(void) {
    //TODO: Identify where the test failed by writing values other
    //than 2.

    pad_with_nops();

    // NB: Has to be this exact instruction because the simulator
    // (Riscvtest.scala) listens for the machine-code that correpsonds
    // to this instruction.
    asm volatile ("csrw tohost, x28");

    // The simulator should detect the above instruction and stop the
    // simulation now.

 infinite_loop:
    goto infinite_loop;
}
