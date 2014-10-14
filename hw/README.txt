The turborav/hw project structure is currently just the directories
fpga_board_specific_code and core. fpga_board_specific_code contains a
Makefile-based vhdl project with the vhdl source of the
Basys2UserDemo. Whereas core contains the TurboRav processor
implemented in Chisel.

The next step is to port the fpga_board_specific_code to Chisel and
instantiate the HW core within the toplevel.
