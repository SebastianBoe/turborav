# Usage:
#	make <module_name>.test            # Run a test on the simulator
#	make <module_name>.v               # Generate verilog
#	make clean                         # Delete generated files
#	make synthesis/<module_name>.srp   # Create synthesis report

# This variable contains the path to an elf program. It is meant to be
# overridden on the command line like:

# 'make ROM=generated/rv32ui-p-simple soc.test'
ROM := generated/startup_program.o

# Include some generic utility functions
include utilities.mk

DIR_SRC     := src/main/scala
DIR_TEST    := src/test/scala
DIR_GEN     := generated
DIR_VLOG    := verilog
DIR_SYNTH   := synthesis

CHISEL_FLAGS_V := \
	--genHarness \
	--backend v \

CROSS ?= riscv64-unknown-elf

CC      := $(CROSS)-gcc
LD      := $(CROSS)-ld
AS      := $(CROSS)-as
OBJCOPY := $(CROSS)-objcopy
OBJDUMP := $(CROSS)-objdump

CFLAGS :=-march=RV32I -Wall -I./
ASFLAGS:=-march=RV32IM --fatal-warnings
OBJDUMP_FLAGS := --architecture=riscv:rv32

# My depdencies are a bit wrong, so as a hack I delete these
# intermediaries to prevent them from being re-used.
.INTERMEDIATE: generated/startup_program.hex generated/startup_program.bin

.PHONY: all %.test %.v

all: soc.v

isa/%:
	$(MAKE) --directory=isa $*

%.test: generated/startup_program.hex $(DIR_TEST)/%test.scala
	sbt "test:run $*test $(ROM)"

%.v: generated/startup_program.hex $(DIR_SRC)/%.scala
	sbt "run $* $(CHISEL_FLAGS_V) --targetDir $(DIR_VLOG)"

generated/startup_program.hex: generated/startup_program.bin
	hexdump -v -e '1/4 "%08X" "\n"' $< > $@

generated/startup_program.bin: $(ROM)
	$(dir_guard)
	${OBJCOPY} -O binary $< $@


rom/startup_program.S:
	$(dir_guard)
	@printf "# Dummy program, can be used for testing\n# Execute: make soc.test\n.globl _start\n_start:\nj _start\nnop\nnop\n" > rom/startup_program.S

ISA_TEST_CC_FLAGS := \
	-m32 \
	-static \
	-nostdlib \
	-nostartfiles \
	-ffreestanding \
	-Wa,-march=RVIMAFDXhwacha

generated/startup_program.o: rom/startup_program.S generated/crt0.o src/misc/turborav.ld
	$(dir_guard)
	${CC} \
		$(ISA_TEST_CC_FLAGS) \
		-fvisibility=hidden \
	 	-Iriscv-tools/riscv-tests/env/p \
		-Iisa/macros/scalar \
		-Tsrc/misc/turborav.ld \
		generated/crt0.o \
		rom/startup_program.S \
		-o $@
# FAIL: update above and below
generated/rv32ui-p-%: isa/rv32ui/%.S generated/crt0.o src/misc/turborav.ld
	${CC} \
		$(ISA_TEST_CC_FLAGS) \
		-fvisibility=hidden \
	 	-Iriscv-tools/riscv-tests/env/p \
		-Iisa/macros/scalar \
		-Tsrc/misc/turborav.ld \
		generated/crt0.o \
		isa/rv32ui/$*.S -o $@

generated/crt0.o: src/misc/crt0.s
	$(dir_guard)
	${CC} \
		-c \
		$(ISA_TEST_CC_FLAGS) \
		$< -o $@

clean:
	rm -rf \
	target \
	project \
	rom \
	$(DIR_GEN) \
	$(DIR_VLOG) \
	$(DIR_SYNTH) \
	$(SYNTHESIS_JUNK)
