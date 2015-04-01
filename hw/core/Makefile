# Usage:
#	make <module_name>.test            # Run a test on the simulator
#	make <module_name>.v               # Generate verilog
#	make <module_name>.out             # Generate c++ simulator
#	make clean                         # Delete generated files
#	make synthesis/<module_name>.srp   # Create synthesis report

# This variable contains the path to an elf program. It is meant to be
# overridden on the command line like:

# 'make ROM="riscv-tools/riscv-tests/isa/rv32ui-p-simple soc.test'
ROM := generated/startup_program.o

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

CHISEL_FLAGS_V := \
	--genHarness \
	--backend v \
	--targetDir $(DIR_VLOG)

modules := \
	$(notdir \
	$(basename \
	$(wildcard \
	$(DIR_SRC)/*.scala)))

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

# My depdencies are a bit wrong, so as a hack I delete these
# intermediaries to prevent them from being re-used.
.INTERMEDIATE: generated/startup_program.hex generated/startup_program.bin

all:
	@echo "No good default make rule yet"

clean:
	rm -rf \
	target \
	project \
	$(DIR_GEN) \
	$(DIR_VLOG) \
	$(DIR_SYNTH) \
	$(SYNTHESIS_JUNK)

# This rule assumes you have already installed the toolchain in our
# riscv-tools submodule.

# Also, oh noes, recursive make! Is recursive make allowed if you have
# a good reason to not change the child makefiles?
isa/%:
	$(MAKE) --directory=isa $*

%.test: generated/startup_program.hex $(DIR_TEST)/%test.scala
	sbt "test:run $*test $(ROM)"

%.v:    generated/startup_program.hex $(DIR_SRC)/%.scala
	sbt "run      $*     $(CHISEL_FLAGS_V)"

$(DIR_SYNTH)/%.srp: $(DIR_SYNTH)/%.xst $(DIR_SYNTH)/%.prj
	xst \
	 -ifn $< \
	 -ofn $@ \
	 -intstyle silent

generated/startup_program.hex: generated/startup_program.bin
	hexdump -v -e '1/4 "%08X" "\n"' $< > $@

generated/startup_program.bin: $(ROM)
	$(dir_guard)
	${OBJCOPY} -O binary $< $@

$(DIR_SYNTH)/%.xst: $(DIR_SYNTH)/%.prj
	@echo "run                           " >  $@
	@echo "-ifn $<                       " >> $@
	@echo "-ofn $(DIR_SYNTH)/$*.ngc      " >> $@
	@echo "-p $(FPGA_MODEL)              " >> $@
	@echo -n "-top " >> $@
	@echo $* | sed -e "s/\b\(.\)/\u\1/g"   >> $@
	@echo "-opt_level 1                  " >> $@
	@echo "-ofmt NGC                     " >> $@
	@echo "-define {SYNTHESIS}           " >> $@
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
