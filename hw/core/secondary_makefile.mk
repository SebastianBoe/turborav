DIR_GEN     := generated
ROM_PROGRAM_NAME := startup_program

$(DIR_GEN)/$(ROM_PROGRAM_NAME).dis: $(DIR_GEN)/$(ROM_PROGRAM_NAME).o
	$(OBJDUMP) $(OBJDUMP_FLAGS) -D $< > $@
    # The disassembly can be done manually for debugging purposes.

$(DIR_GEN)/$(ROM_PROGRAM_NAME).o: rom/$(ROM_PROGRAM_NAME).S
	$(dir_guard)
	${AS} ${ASFLAGS} $< -o $@
