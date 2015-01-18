main:
    li x1, 1
    li x2, 0x10000000
    nop
    nop
    nop
    nop
    nop
    sw x1, 4(x2)
    nop
    nop
    nop
    nop
    nop
    lw x2, 4(x2)
    b main
