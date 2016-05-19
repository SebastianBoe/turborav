/*
    In this startup program we do a memcpy to copy initialized data
	from the ROM to RAM. Also, we initialize the stack pointer to the
	end of the RAM.
*/

    .section .custom_startup_file_section
    .global _crt0
_crt0:
    /* Initialize the global pointer (x3)*/
	/*  NB: Hardcodes the assumption that the initialized data starts */
	/*  at 0x1000_0000 */
    lui gp,0x10000

    /* Initialize the stack pointer */
    lui	x2,   %hi(ram_end)
	add	x2,x2,%lo(ram_end)

    add x28, gp, 0

    /* Load the start and end address of the bss section into register
	26 and 27. */
	lui	x27,    %hi(bss_start)
	add	x27,x27,%lo(bss_start)

	lui	x26,    %hi(bss_end)
	add	x26,x26,%lo(bss_end)

    /* Skip bss clearing if the bss sections is empty. */
    beq x27,x26, memclear_loop_end

    /* Begin writing all zeros to the (s)bss sections. */
memclear_loop:
    sw  x0, 0(x27)
    add x27, x27, 4
    blt x27, x26, memclear_loop
memclear_loop_end:

	lui	x29,   %hi(initialized_data_load_start)
	add	x29,x29,%lo(initialized_data_load_start)

    beq	x28,x29,main

	lui	x30,   %hi(initialized_data_size)
	add	x30,x30,%lo(initialized_data_size)

    /* x6 contains initialized_data_end */
    add x6, x30, gp

    beq	x6,x28,main

    add x31, x0, x0
memcpy_loop:
    lw x5, 0(x29)
    sw x5, 0(x28)
    add x29, x29, 4
    add x28, x28, 4
    add x31, x31, 4
    bne x31, x30, memcpy_loop

    j main

/*
 If a main function has not been defined, create one that jumps to
 _start. This is useful for programs without a main function like
 the RISC-V assembly test.
*/
    .weak main
main:
    j _start

/*
 This assembly routine should never be needed at runtime, but is
 needed at compile time by the main assembly routine. _start should
 be provided by riscv tests.
*/
    .weak _start
_start:
    j _start
