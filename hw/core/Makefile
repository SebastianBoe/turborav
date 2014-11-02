# Usage:
#	make <module_name>.test				# Run testbench
#	make <module_name>.v				# Generate verilog
#	make <module_name>.out				# Generate c++ backend
#	make clean							# Delete generated files
#	make synthesis/<module_name>.srp 	# Create synthesis report

#TODO: Change signature to "make <module_name>.synth"

# Include some generic utility functions
include utilities.mk

DIR_SRC     = src/main/scala
DIR_TEST    = src/test/scala
DIR_GEN     = generated
DIR_VLOG    = verilog
DIR_SYNTH = synthesis

FPGA_MODEL = xc3s250e-cp132-5

CHISEL_FLAGS_C := \
	--genHarness \
	--backend c \
	--targetDir $(DIR_GEN) \
	--compile \
	--test \
	--vcd

CHISEL_FLAGS_V := \
	--genHarness \
	--backend v \
	--targetDir $(DIR_VLOG)

modules := \
	$(notdir \
	$(basename \
	$(wildcard \
	$(DIR_SRC)/*.scala)))

.PHONY: all

all:
	@echo "No good default make rule yet"

clean:
	rm -rf \
	target \
	project \
	$(DIR_GEN) \
	$(DIR_VLOG) \
	$(DIR_SYNTH)

%.test: $(DIR_TEST)/%test.scala
	sbt "test:run $*test $(CHISEL_FLAGS_C)"

%.v: $(DIR_SRC)/%.scala
	sbt "run      $*     $(CHISEL_FLAGS_V)"

%.out: $(DIR_SRC)/%.scala
	sbt "run      $*     $(CHISEL_FLAGS_C)"

$(DIR_SYNTH)/%.srp: $(DIR_SYNTH)/%.xst $(DIR_SYNTH)/%.prj
	xst \
	 -ifn $< \
	 -ofn $@ \
	 -intstyle silent

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
