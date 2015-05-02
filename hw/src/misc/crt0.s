_crt0:
    lui	x1,   %hi(data_start)
	add	x1,x1,%lo(data_start)

	lui	x2,   %hi(data_load_start)
	add	x2,x2,%lo(data_load_start)

    beq	x1,x2,_start

	lui	x3,   %hi(data_size)
	add	x3,x3,%lo(data_size)

	lui	x6,   %hi(data_end)
	add	x6,x6,%lo(data_end)

    beq	x6,x1,_start

    add x5, x0, x0
memcpy_loop:
    lw x4, 0(x2)
    sw x4, 0(x1)
    add x2, x2, 4
    add x1, x1, 4
    add x5, x5, 4
    bne x5, x3, memcpy_loop

    j _start
