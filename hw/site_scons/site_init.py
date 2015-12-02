def sim_builder_generator(source, target, env, for_signature):
    cmd = " scala "
    cmd += " -classpath $CLASSPATH "
    cmd += " TurboRav.TurboRavTestRunner "
    cmd += " Riscvtest "
    cmd += " $TARGET_DIR "
    cmd += " $ROM "
    cmd += " 12 "
    cmd += " 8 "
    cmd += " False &> $TARGET_DIR/log.txt || cat $TARGET_DIR/log.txt; "

    cmd += " vcd2fst $TARGET_DIR/Soc.vcd $TARGET_DIR/wave.fst;"
    cmd += " rm --force $TARGET_DIR/Soc.vcd "
    return cmd

sim_builder = Builder(
    generator = sim_builder_generator
)
