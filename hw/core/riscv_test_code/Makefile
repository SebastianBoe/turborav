# Makefile for assembling a RiscV assembly program.

# Assumes that a RiscV toolchain with the prefix $(TC) is installed on
# path. Find the toolchain at
# https://github.com/ucb-bar/riscv-gnu-toolchain (The Linux version
# did not work for me, only the Newlib version, YMMV).

# ToolChain prefix
TC?=riscv64-unknown-elf-

# Setting the architecture to RV32I will prevent 64 bit instructions
# and other fancy instructions from being generated I think.
CFLAGS=-march=RV32I

# Set to numeric to get registers x0-31, instead of weird ABI aliases.
# These options were found in objdump --help.
OBJDUMP_FLAGS=-M numeric --architecture=riscv:rv32

all:
	$(TC)gcc -c $(CFLAGS) 1_+_2.s
	$(TC)objdump $(OBJDUMP_FLAGS) -D 1_+_2.o

clean:
	rm *.o
