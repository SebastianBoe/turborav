import os

def sim_builder_generator(source, target, env, for_signature):
    cmd = " scala "
    cmd += " -classpath $CLASSPATH "
    cmd += " TurboRav.TurboRavTestRunner "
    cmd += env['test_runner_args'].replace(",", " ")
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

def get_classpath():
    CHISEL_JAR     = "/usr/share/scala/chisel/chisel.jar"
    COMMONS_IO_JAR = "/usr/share/java/commons-io/commons-io.jar"
    return "{0}:{1}".format(CHISEL_JAR, COMMONS_IO_JAR)
