main:
    li x1, 0x1
    li x4, 0x10000000
    nop
    nop
    nop
    nop
    nop
    sw x1, 0x4(x4)
    nop
    nop
    nop
    nop
    nop
    lw x3, 0x4(x4)
    nop
    nop
    j main
