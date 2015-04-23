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
#HDL_FILES = $(wildcard src/*.vhd) $(wildcard src/*.v)
HDL_FILES = ../core/verilog/Soc.v
INTSTYLE = silent
#################################################################
# Makefile body
#################################################################

.PHONY: all program bitfile
all: program

# Generate the prj file, which is sort of like
# a list of all the source files that you intend to use.
build/project.prj: $(HDL_FILES)
	mkdir -p build
	@find .. -name 'Soc.v'   -printf "verilog work %p\n" > $@

build/xst_script.xst: build/project.prj
	@echo "run                           " >  $@
	@echo "-ifn $<        				 " >> $@
	@echo "-ofn build/$(PROJECT_NAME).ngc" >> $@
	@echo "-p $(FPGA_MODEL)              " >> $@
	@echo "-top $(TOP_MODULE)            " >> $@
	@echo "-opt_level 1                  " >> $@
	@echo "-ofmt NGC                     " >> $@
	@echo "-work_lib work                " >> $@
	@echo >> $@

build/$(PROJECT_NAME).ngc: build/xst_script.xst
	xst \
	 -ifn build/xst_script.xst \
	 -ofn build/$(PROJECT_NAME).srp \
	 -intstyle $(INTSTYLE)

	@rm -r $(TOP_MODULE).lso _xmsgs xst

build/native_generic_database.ngd: build/$(PROJECT_NAME).ngc $(PROJECT_NAME).ucf
	ngdbuild \
	-p $(FPGA_MODEL) \
	-sd build \
	-dd build \
	-uc turborav.ucf \
	-intstyle $(INTSTYLE) \
	-quiet \
	$(PROJECT_NAME) \
	build/native_generic_database.ngd > build/ngdbuild.log

	@rm -r _xmsgs xlnx_auto_0_xdb

build/design.ncd: build/native_generic_database.ngd
	map \
	-intstyle $(INTSTYLE) \
	$(MUTLITHREADED_MAP_CMD_LINE_OPTION) \
	-ol std \
	-p $(FPGA_MODEL) \
	-o build/design.ncd \
	-timing \
	build/native_generic_database.ngd \
	build/physical_constraints_file.pcf

build/design_routed.ncd: build/design.ncd
	par \
	$(MUTLITHREADED_MAP_CMD_LINE_OPTION) \
	-k \
	-p \
	-w \
	-ol std \
	-intstyle $(INTSTYLE) \
	build/design.ncd \
	$@ \
	build/physical_constraints_file.pcf

build/$(PROJECT_NAME).bit: build/design_routed.ncd
	bitgen \
	-intstyle $(INTSTYLE) \
	-f $(PROJECT_NAME).ut \
	$< \
	$@ \
	build/physical_constraints_file.pcf

	@rm -r xilinx_device_details.xml _xmsgs/

bitfile: build/$(BITFILE)

program: bitfile
	djtgcfg prog -d Basys2 --index 0 --file build/$(BITFILE) > build/programming.log

clean:
	rm -rf build/*
