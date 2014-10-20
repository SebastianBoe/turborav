SBT          ?= sbt
SBT_FLAGS    ?= -Dsbt.log.noformat=true

SRC_DIR     = src/main/scala
TEST_DIR    = src/test/scala
GEN_DIR     = generated
VLOG_DIR    = verilog
OUTPUT_DIR  = output

CHISEL_FLAGS   := --targetDir $(GEN_DIR)
CHISEL_FLAGS_C := --genHarness --compile --test --backend c $(CHISEL_FLAGS)
CHISEL_FLAGS_V := --genHarness --backend v --targetDir $(VLOG_DIR)

modules := $(notdir $(basename $(wildcard $(SRC_DIR)/*.scala)))

all:
	@echo "No good default make rule yet"

clean:
	rm -rf target project $(GEN_DIR) $(VLOG_DIR) $(OUTPUT_DIR)

outputdir:
	mkdir -p $(OUTPUT_DIR)

%.out: $(SRC_DIR)/%.scala outputdir
	$(SBT) $(SBT_FLAGS) \
	"run $(notdir $(basename $<)) $(CHISEL_FLAGS_C)" \
	| tee $(OUTPUT_DIR)/$@

%.test: $(TEST_DIR)/%test.scala outputdir
	$(SBT) $(SBT_FLAGS) \
	"test:run $(notdir $(basename $<)) $(CHISEL_FLAGS_C)" \
	| tee $(OUTPUT_DIR)/$@

%.hex: $(SRC_DIR)/%.scala
	$(SBT) $(SBT_FLAGS) \
	"run $(notdir $(basename $<)) --backend flo --genHarness --compile --test $(CHISEL_FLAGS)"

%.v: $(SRC_DIR)/%.scala
	$(SBT) $(SBT_FLAGS) \
	"run $(notdir $(basename $<)) $(CHISEL_FLAGS_V)"
