# Usage:
#	make <module_name>.test	# Run testbench
#	make <module_name>.v	# Generate verilog
#	make <module_name>.out	# Generate c++ backend
#	make clean				# Delete generated files

DIR_SRC     = src/main/scala
DIR_TEST    = src/test/scala
DIR_GEN     = generated
DIR_VLOG    = verilog

CHISEL_FLAGS_C := \
	--genHarness \
	--backend c \
	--targetDir $(DIR_GEN) \
	--compile \
	--test \
	--vcd

CHISEL_FLAGS_V := \
	--genHarness \
	--backend v \
	--targetDir $(DIR_VLOG)

modules := \
	$(notdir \
	$(basename \
	$(wildcard \
	$(DIR_SRC)/*.scala)))

.PHONY: all

all:
	@echo "No good default make rule yet"

clean:
	rm -rf \
	target \
	project \
	$(DIR_GEN) \
	$(DIR_VLOG)

%.test: $(DIR_TEST)/%test.scala
	sbt "test:run $*test $(CHISEL_FLAGS_C)"

%.v: $(DIR_SRC)/%.scala
	sbt "run      $*     $(CHISEL_FLAGS_V)"

%.out: $(DIR_SRC)/%.scala
	sbt "run      $*     $(CHISEL_FLAGS_C)"
