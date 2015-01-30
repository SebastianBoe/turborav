main:
    li x1, 0x10000000
    li x2, 'H'
    nop
    nop
    nop
    nop
    nop
    sw x2, (x1)
    nop
    nop
    nop
    nop
    nop
    lw x3, (x1)
    nop
    nop
    nop
    nop
    nop
    j main
