import os

def sim_builder_generator(source, target, env, for_signature):
    cmd = " scala "
    cmd += " -classpath $CLASSPATH "
    cmd += " TurboRav.TurboRavTestRunner "
    cmd += " $MODULE "
    cmd += " $TARGET_DIR "
    cmd += " $ROM "
    cmd += " 4 "
    cmd += " 4 "
    cmd += " False &> $TARGET_DIR/log.txt || cat $TARGET_DIR/log.txt; "

    cmd += " vcd2fst $TARGET_DIR/${TOP_MODULE}.vcd $TARGET_DIR/wave.fst;"
    cmd += " rm $TARGET_DIR/${TOP_MODULE}.vcd "
    return cmd

sim_builder = Builder(
    generator = sim_builder_generator,
    ENV = os.environ
)
