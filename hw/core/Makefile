SRC_DIR     = src/main/scala
TEST_DIR    = src/test/scala
GEN_DIR     = generated
VLOG_DIR    = verilog
OUTPUT_DIR  = output

CHISEL_FLAGS   := --targetDir $(GEN_DIR)
CHISEL_FLAGS_C := --genHarness --compile --test --backend c $(CHISEL_FLAGS)
CHISEL_FLAGS_V := --genHarness --backend v --targetDir $(VLOG_DIR)

modules := $(notdir $(basename $(wildcard $(SRC_DIR)/*.scala)))

.PHONY: all

all:
	@echo "No good default make rule yet"

clean:
	rm -rf target project $(GEN_DIR) $(VLOG_DIR) $(OUTPUT_DIR)

outputdir:
	mkdir -p $(OUTPUT_DIR)

%.out: $(SRC_DIR)/%.scala outputdir
	sbt "run $(notdir $(basename $<)) $(CHISEL_FLAGS_C)" \
	| tee $(OUTPUT_DIR)/$@
# Remove bash-style coloring from output log
	@sed -i -r "s/\x1B\[([0-9]{1,2}(;[0-9]{1,2})?)?[mGK]//g" $(OUTPUT_DIR)/$@

%.test: $(TEST_DIR)/%test.scala outputdir
	sbt "test:run $(notdir $(basename $<)) $(CHISEL_FLAGS_C)" \
	| tee $(OUTPUT_DIR)/$@
# Remove bash-style coloring from output log
	@sed -i -r "s/\x1B\[([0-9]{1,2}(;[0-9]{1,2})?)?[mGK]//g" $(OUTPUT_DIR)/$@

%.hex: $(SRC_DIR)/%.scala
	sbt "run $(notdir $(basename $<)) --backend flo --genHarness --compile --test $(CHISEL_FLAGS)"

%.v: $(SRC_DIR)/%.scala
	sbt "run $(notdir $(basename $<)) $(CHISEL_FLAGS_V)"

