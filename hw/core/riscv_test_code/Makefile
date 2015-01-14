# Makefile for assembling a RiscV assembly programs.
# Find the toolchain at
# https://github.com/ucb-bar/riscv-gnu-toolchain.

CROSS?=riscv-

CC=$(CROSS)gcc
LD=$(CROSS)ld
AS=$(CROSS)as
OBJCOPY=$(CROSS)objcopy
OBJDUMP=$(CROSS)objdump

CFLAGS=-march=RV32I -Wall -I./
ASFLAGS=-march=RV32I
OBJDUMP_FLAGS=-M numeric --architecture=riscv:rv32

.PHONY: all
all: 1_+_2.hex

%.hex: %.bin
	hexdump -v -e '1/4 "%08X" "\n"' $< > $@

%.bin: %.o
	${OBJCOPY} -O binary $< $@

%.o: %.S
	${AS} ${ASFLAGS} $< -o $@

%.o: %.c
	${CC} ${CFLAGS} -c $< -o $@

.PHONY: clean
clean:
	rm -rf *.o *.bin *.hex