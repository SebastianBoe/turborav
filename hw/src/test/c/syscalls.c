#include <stdint.h>
#include <sys/stat.h>

// Minimal syscalls for simulating libc-dependent programs on TurboRav
// and getting printf in simulation.

// Based on https://sourceware.org/newlib/libc.html#Stubs

char * sbrk(int incr) {
    extern char _end;		/* Defined by the linker */
    static char *heap_end;
    char *prev_heap_end;

    if (heap_end == 0) {
        heap_end = &_end;
    }
    prev_heap_end = heap_end;
    heap_end += incr;
    return prev_heap_end;
}

int fstat(int file, struct stat *st) {
    st->st_mode = S_IFCHR;
    return 0;
}

int isatty(int file) {
    return 1;
}

int read(int file, char *ptr, int len) {
    return 0;
}

int lseek(int file, int ptr, int dir) {
    return 0;
}

#pragma GCC push_options
#pragma GCC optimize ("O0")
int write(int file, char *ptr, int len) {
    // Don't inline outbyte because the simulator assumes that c is in
    // the function argument register x10 (AKA a0).
    void __attribute__((noinline)) outbyte(char c) {
        // We insert NOP's because the simulator will erronously print
        // stuff when the CPU's branch predictor mispredicts and
        // speculatively fetches the csrw instruction.

        asm volatile ("nop");
        asm volatile ("nop");
        asm volatile ("nop");
        asm volatile ("nop");
        asm volatile ("nop");
        asm volatile ("nop");
        asm volatile ("nop");
        asm volatile ("nop");
        asm volatile ("nop");
        asm volatile ("nop");

        // When the simulator (Riscvtest.scala) sees the below instruction
        // it will print the character in x10.

        asm volatile ("csrw tohost, x29");
    }

    while(len--)
        outbyte(*ptr++);

    return len;
}
#pragma GCC pop_options

int close(int file) {
  return -1;
}
