# The reader is assumed to either know what each Xilinx program does
# or be willing to read the Xilinx "Command Line Tools User Guide.pdf"
# and the "XST User Guide.pdf"

####################################################################
# Project-dependent variables
####################################################################
PROJECT_NAME	= turborav
FPGA_MODEL		= xc3s250e-cp132-5
TOP_MODULE		= Soc
BITFILE			= $(PROJECT_NAME).bit
UCF_FILE		= $(PROJECT_NAME).ucf
UT_FILE			= $(PROJECT_NAME).ut

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
HDL_FILE = is-set-by-user
INTSTYLE = silent
#################################################################
# Makefile body
#################################################################

# Generate the prj file, which is sort of like
# a list of all the source files that you intend to use.
project.prj: $(HDL_FILE)
	printf "verilog work $(HDL_FILE)\n" > $@

xst_script.xst: project.prj
	@echo "run                           " >  $@
	@echo "-ifn $<        				 " >> $@
	@echo "-ofn $(PROJECT_NAME).ngc      " >> $@
	@echo "-p $(FPGA_MODEL)              " >> $@
	@echo "-top $(TOP_MODULE)            " >> $@
	@echo "-opt_level 1                  " >> $@
	@echo "-ofmt NGC                     " >> $@
	@echo "-work_lib work                " >> $@
	@echo >> $@

$(PROJECT_NAME).ngc: xst_script.xst
	xst \
	 -ifn xst_script.xst \
	 -ofn $(PROJECT_NAME).srp \
	 -intstyle $(INTSTYLE)

native_generic_database.ngd: $(PROJECT_NAME).ngc $(UCF_FILE)
	ngdbuild \
	-p $(FPGA_MODEL) \
	-sd generated \
	-dd generated \
	-uc $(UCF_FILE) \
	-intstyle $(INTSTYLE) \
	-quiet \
	$(PROJECT_NAME) \
	native_generic_database.ngd > ngdbuild.log

design.ncd: native_generic_database.ngd
	map \
	-intstyle $(INTSTYLE) \
	$(MUTLITHREADED_MAP_CMD_LINE_OPTION) \
	-ol std \
	-p $(FPGA_MODEL) \
	-o design.ncd \
	-timing \
	native_generic_database.ngd \
	physical_constraints_file.pcf

design_routed.ncd: design.ncd
	par \
	$(MUTLITHREADED_MAP_CMD_LINE_OPTION) \
	-k \
	-p \
	-w \
	-ol std \
	-intstyle $(INTSTYLE) \
	design.ncd \
	$@ \
	physical_constraints_file.pcf

$(PROJECT_NAME).bit: design_routed.ncd
	bitgen \
	-intstyle $(INTSTYLE) \
	-f $(UT_FILE) \
	$< \
	$@ \
	physical_constraints_file.pcf

bitfile: $(BITFILE)

program: bitfile
	djtgcfg prog -d Basys2 --index 0 --file $(BITFILE) > programming.log
