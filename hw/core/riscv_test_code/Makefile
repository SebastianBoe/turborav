# Makefile for assembling a RiscV assembly program.

# Assumes that a RiscV toolchain with the prefix $(TC) is installed on
# path. Find the toolchain at
# https://github.com/ucb-bar/riscv-gnu-toolchain (The Linux version
# did not work for me, only the Newlib version, YMMV).

# ToolChain prefix
TC=riscv64-unknown-elf-

# Setting the architecture to RV32I will prevent 64 bit instructions
# and other fancy instructions from being generated I think.
CFLAGS=-march=RV32I

all:
	$(TC)gcc -c $(CFLAGS) 1_+_2.s
	$(TC)objdump -D 1_+_2.o
