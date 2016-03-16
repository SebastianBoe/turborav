import os

def sim_builder_generator(source, target, env, for_signature):
    cmd = " scala "
    cmd += " -classpath $CLASSPATH "
    cmd += " TurboRav.TurboRavTestRunner "
    cmd += " --target-directory $TARGET_DIR "
    cmd += " --rom $ROM "
    cmd += " --no-fpga "
    cmd += " $MODULE "
    cmd += " &> $TARGET_DIR/log.txt || cat $TARGET_DIR/log.txt; "

    cmd += " vcd2fst $TARGET_DIR/${TOP_MODULE}.vcd $TARGET_DIR/wave.fst;"
    cmd += " rm $TARGET_DIR/${TOP_MODULE}.vcd "
    return cmd

sim_builder = Builder(
    generator = sim_builder_generator,
    ENV = os.environ
)
