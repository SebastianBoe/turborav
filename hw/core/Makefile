# Usage:
#	make <module_name>.test            # Run a test on the simulator
#	make <module_name>.v               # Generate verilog
#	make <module_name>.out             # Generate c++ simulator
#	make clean                         # Delete generated files
#	make synthesis/<module_name>.srp   # Create synthesis report
#	make generated/startup_program.dis # Disassemble ROM contents

#TODO: Change signature to "make <module_name>.synth"

# Include some generic utility functions
include utilities.mk

DIR_SRC     := src/main/scala
DIR_TEST    := src/test/scala
DIR_GEN     := generated
DIR_VLOG    := verilog
DIR_SYNTH   := synthesis

# Perhaps call xst in it's own directory to not get this junk in
# hw/core
SYNTHESIS_JUNK := \
	xst \
	_xmsgs/ \
	hs_err_pid1674.log \
	*.lso

FPGA_MODEL := xc3s250e-cp132-5

CHISEL_FLAGS_C := \
	--genHarness \
	--backend c \
	--targetDir $(DIR_GEN) \
	--compile \
	--test \
	--vcd \
	--debug

CHISEL_FLAGS_V := \
	--genHarness \
	--backend v \
	--targetDir $(DIR_VLOG)

modules := \
	$(notdir \
	$(basename \
	$(wildcard \
	$(DIR_SRC)/*.scala)))

ROM_PROGRAM_NAME := startup_program
ROM := $(DIR_GEN)/$(ROM_PROGRAM_NAME).hex

CROSS ?= riscv64-unknown-elf-

CC      := $(CROSS)gcc
LD      := $(CROSS)ld
AS      := $(CROSS)as
OBJCOPY := $(CROSS)objcopy
OBJDUMP := $(CROSS)objdump

CFLAGS :=-march=RV32I -Wall -I./
ASFLAGS:=-march=RV32I --fatal-warnings
OBJDUMP_FLAGS := --architecture=riscv:rv32

.PHONY: all

# Don't delete the ROM hex file contents, it is useful when debugging
.SECONDARY: $(ROM)

all:
	@echo "No good default make rule yet"

clean:
	rm -rf \
	target \
	project \
	$(DIR_GEN) \
	$(DIR_VLOG) \
	$(SYNTHESIS_JUNK) \
	$(DIR_SYNTH)

%.test: $(ROM) $(DIR_TEST)/%test.scala
	sbt "test:run $*test $(CHISEL_FLAGS_C)"

%.v:    $(ROM) $(DIR_SRC)/%.scala
	sbt "run      $*     $(CHISEL_FLAGS_V)"

%.out:  $(ROM) $(DIR_SRC)/%.scala
	sbt "run      $*     $(CHISEL_FLAGS_C)"

$(DIR_SYNTH)/%.srp: $(DIR_SYNTH)/%.xst $(ROM) $(DIR_SYNTH)/%.prj
	xst \
	 -ifn $< \
	 -ofn $@ \
	 -intstyle silent

%.hex: %.bin
	hexdump -v -e '1/4 "%08X" "\n"' $< > $@

%.bin: %.o
	${OBJCOPY} -O binary $< $@

$(DIR_GEN)/$(ROM_PROGRAM_NAME).dis: $(DIR_GEN)/$(ROM_PROGRAM_NAME).o
	$(OBJDUMP) $(OBJDUMP_FLAGS) -D $< > $@
    # The disassembly can be done manually for debugging purposes.

$(DIR_GEN)/$(ROM_PROGRAM_NAME).o: rom/$(ROM_PROGRAM_NAME).S
	$(dir_guard)
	${AS} ${ASFLAGS} $< -o $@

$(DIR_SYNTH)/%.xst: $(DIR_SYNTH)/%.prj
	@echo "run                           " >  $@
	@echo "-ifn $<                       " >> $@
	@echo "-ofn $(DIR_SYNTH)/$*.ngc      " >> $@
	@echo "-p $(FPGA_MODEL)              " >> $@
	@echo -n "-top " >> $@
	@echo $* | sed -e "s/\b\(.\)/\u\1/g"   >> $@
	@echo "-opt_level 1                  " >> $@
	@echo "-ofmt NGC                     " >> $@
	@echo "-work_lib work                " >> $@
	@echo >> $@

# Create the Xilinx .prj file that contains a listing of source files
# to be synthesized. Assumues that all files (excluding the harnesses)
# in $(DIR_SYNTH) are synthesizable verilog files.
$(DIR_SYNTH)/%.prj: %.v
# We start with a utilities.dir_guard because this is the first file
# created in DIR_SYNTH
	$(dir_guard)
# Find the files in DIR_VERILOG that don't match *-harness.v and use
# format print to create the project file format that Xilinx
# needs. Finally as a hack remove the first line because I couldn't
# figure out how to create it properly.
	find $(DIR_VLOG) -name \*-harness.v -o -printf "verilog work %p\n" |\
	tail -n +2 > $@
