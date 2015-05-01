# Usage:
#	make <module_name>.test                     # Run a unit test on the simulator
#	make <module_name>.v                        # Generate verilog
#   make ROM=path_to_elf_file soc.test          # Run a custom program on Turborav
#   make ROM=generated/rv32ui-p-addi riscv.test # Run a riscv test

# Include some generic utility functions
include utilities.mk

NUM_PIN_INPUTS  := 12 # See turborav.ucf
NUM_PIN_OUTPUTS := 8

# Path to an elf file.
ROM := generated/startup_program.o

DIR_SRC     := src/main/scala
DIR_TEST    := src/test/scala
DIR_GEN     := generated
DIR_VLOG    := verilog
DIR_SYNTH   := synthesis

CROSS ?= riscv64-unknown-elf

CC      := $(CROSS)-gcc
LD      := $(CROSS)-ld
AS      := $(CROSS)-as
OBJCOPY := $(CROSS)-objcopy

CFLAGS :=-march=RV32I -Wall -I./
ASFLAGS:=-march=RV32IM --fatal-warnings

# My depdencies are a bit wrong, so as a hack I delete these
# intermediaries to prevent them from being re-used.
.INTERMEDIATE: generated/startup_program.hex generated/startup_program.bin

.PHONY: all %.test %.v

all: soc.v

isa/%:
	$(MAKE) --directory=isa $*

%.test: generated/startup_program.hex $(DIR_TEST)/%test.scala
	sbt "test:run $*test $(ROM) $(NUM_PIN_INPUTS) $(NUM_PIN_OUTPUTS)"

%.v:    generated/startup_program.hex $(DIR_SRC)/%.scala
	sbt "run      $*     $(ROM) $(NUM_PIN_INPUTS) $(NUM_PIN_OUTPUTS)"

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
