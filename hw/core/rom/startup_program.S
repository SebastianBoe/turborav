main:
    li x1, 0xb0e
    li x1, 0xb0e
    li x2, 0x10000000
    nop
    nop
    nop
    nop
    nop
    sw x1, 0(x2)
    nop
    nop
    nop
    nop
    nop
    lw x2, 0(x2)
    b main
