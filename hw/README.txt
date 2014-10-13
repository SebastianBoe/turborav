The turborav/hw project structure is currently just
fpga_board_specific_code. It contains a Makefile-based vhdl project
with the vhdl source of the Basys2UserDemo.

The next step is to port the fpga_board_specific_code to Chisel and
instantiate the HW core within the toplevel. It might be beneficial to
have the HW core in turborav/hw/core to seperate the dirty details of
the FPGA from the hopefully more beatiful core code containg processor
and memory.
