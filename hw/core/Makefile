# Usage:
#	make <module_name>.test                     # Run a unit test on the simulator
#	make verilog/<module_name>.v                # Generate verilog
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

.PHONY: all %.test %.v program bitfile

all: program

isa/%:
	$(MAKE) --directory=isa $*

%.test:      generated/startup_program.hex $(DIR_TEST)/%test.scala
	sbt "test:run $*test $(ROM) $(NUM_PIN_INPUTS) $(NUM_PIN_OUTPUTS)"

verilog/%.v: generated/startup_program.hex $(DIR_SRC)/%.scala
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

# The reader is assumed to either know what each Xilinx program does
# or be willing to read the Xilinx "Command Line Tools User Guide.pdf"
# and the "XST User Guide.pdf"

####################################################################
# Project-dependent variables 
####################################################################
PROJECT_NAME = turborav
FPGA_MODEL = xc3s250e-cp132-5
TOP_MODULE = Soc
BITFILE = $(PROJECT_NAME).bit
UCF_FILE = src/synthesis/$(PROJECT_NAME).ucf
UT_FILE  = src/synthesis/$(PROJECT_NAME).ut

# Multithreading the map command. It varies from fpga to fpga if this
# can be enabled. This is a bit ugly, but the map command will not
# accept -mt off so to conditionally support mutlithreading we have to
# conditionally create the command line option "-mt on " or "".  TODO:
# find a more idiomatic way of doing this.
MULTITHREADED_MAP = off 
ifeq ($(MULTITHREADED_MAP), on) 
	MUTLITHREADED_MAP_CMD_LINE_OPTION=-mt on
else
	MUTLITHREADED_MAP_CMD_LINE_OPTION=
endif

#################################################################
# Project independent variables
#################################################################
HDL_FILES = $(DIR_VLOG)/Soc.v
INTSTYLE = silent
#################################################################
# Makefile body
#################################################################

$(DIR_VLOG)/$(TOP_MODULE):
	make Soc.v

# Generate the prj file, which is sort of like
# a list of all the source files that you intend to use.
generated/project.prj: $(HDL_FILES)
	mkdir -p generated
	printf "verilog work $(HDL_FILES)\n" > $@

generated/xst_script.xst: generated/project.prj
	@echo "run                           " >  $@
	@echo "-ifn $<        				 " >> $@
	@echo "-ofn generated/$(PROJECT_NAME).ngc" >> $@
	@echo "-p $(FPGA_MODEL)              " >> $@
	@echo "-top $(TOP_MODULE)            " >> $@
	@echo "-opt_level 1                  " >> $@
	@echo "-ofmt NGC                     " >> $@
	@echo "-work_lib work                " >> $@
	@echo >> $@

generated/$(PROJECT_NAME).ngc: generated/xst_script.xst
	xst \
	 -ifn generated/xst_script.xst \
	 -ofn generated/$(PROJECT_NAME).srp \
	 -intstyle $(INTSTYLE)

	@rm -r $(TOP_MODULE).lso _xmsgs xst

generated/native_generic_database.ngd: generated/$(PROJECT_NAME).ngc $(UCF_FILE)
	ngdbuild \
	-p $(FPGA_MODEL) \
	-sd generated \
	-dd generated \
	-uc $(UCF_FILE) \
	-intstyle $(INTSTYLE) \
	-quiet \
	$(PROJECT_NAME) \
	generated/native_generic_database.ngd > generated/ngdbuild.log

	@rm -r _xmsgs xlnx_auto_0_xdb

generated/design.ncd: generated/native_generic_database.ngd
	map \
	-intstyle $(INTSTYLE) \
	$(MUTLITHREADED_MAP_CMD_LINE_OPTION) \
	-ol std \
	-p $(FPGA_MODEL) \
	-o generated/design.ncd \
	-timing \
	generated/native_generic_database.ngd \
	generated/physical_constraints_file.pcf

	@rm $(TOP_MODULE)_map.xrpt

generated/design_routed.ncd: generated/design.ncd
	par \
	$(MUTLITHREADED_MAP_CMD_LINE_OPTION) \
	-k \
	-p \
	-w \
	-ol std \
	-intstyle $(INTSTYLE) \
	generated/design.ncd \
	$@ \
	generated/physical_constraints_file.pcf

	@rm $(TOP_MODULE)_par.xrpt


generated/$(PROJECT_NAME).bit: generated/design_routed.ncd
	bitgen \
	-intstyle $(INTSTYLE) \
	-f $(UT_FILE) \
	$< \
	$@ \
	generated/physical_constraints_file.pcf

	@rm -r xilinx_device_details.xml _xmsgs/

bitfile: generated/$(BITFILE)

program: bitfile
	djtgcfg prog -d Basys2 --index 0 --file generated/$(BITFILE) > generated/programming.log

clean:
	rm -rf \
	target \
	project \
	rom \
	$(DIR_GEN) \
	$(DIR_VLOG) \
	$(DIR_SYNTH) \
	$(SYNTHESIS_JUNK)
