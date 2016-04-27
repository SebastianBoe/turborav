# blinky leds blinks blinky LED

.globl _start
_start:
    li x1, 0x20000000
    li x2, 0x1

    li x7, 0x500000
    li x4, 0x0
loop1:
    add x4, x4, 0x1
    bne x4, x7, loop1
    sw x2, (x1)

    li x4, 0x0
loop2:
    add x4, x4, 0x1
    bne x4, x7, loop2
    sw x0, (x1)

    li x4, 0x0
    j loop1

j _start
