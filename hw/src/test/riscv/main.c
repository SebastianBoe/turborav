// Label entry point for all riscv tests.
void _start(void);

// Entry point that the startup file crt0.s jumps to.
void main() { _start(); }
