The turborav/hw project structure is currently just the directories
toplevel and core. toplevel contains a Makefile-based vhdl project
with the vhdl source of the Basys2UserDemo. Whereas core contains the
TurboRav processor implemented in Chisel.

The next step is to port toplevel to Chisel and instantiate the HW
core within it.
