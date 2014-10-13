A port of the Basys2 User Demo Xilinx ISE project to a 
Makefile based build system.

It strives to be generic as fas as Xilinx FPGA-projects go and to
generate as many Xilinx-required build files as possible.
Being generic may allow me to use it as a base for new projects 
and generating files like the .prj file is just common sense.

Dependencies
A Basys2 FPGA board.
Software from Digilent for flashing the FPGA. On arch this can be
installed with the package adept-utilities from the AUR.
Xilinx ISE 14.x

Project structure
src 
Contains all non-generated files except for the Makefile itself. This
includes HDL code, and some synthesis input files to Xilinx.

build
Contains all generated files.
