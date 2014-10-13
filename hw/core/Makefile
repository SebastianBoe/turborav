SBT          ?= sbt
SBT_FLAGS    ?= -Dsbt.log.noformat=true

CHISEL_FLAGS := 

executables := $(notdir $(basename $(wildcard *.scala)))

all:
	sbt "run"

clean:
	rm -f *.cpp *.o *.h *.hex *.v *.out *.flo $(executables)
	rm -rf target project

%.out: %.scala
	$(SBT) $(SBT_FLAGS) "run $(notdir $(basename $<)) --genHarness --compile --test --backend c $(CHISEL_FLAGS)" | tee $@

%.hex: %.scala
	$(SBT) $(SBT_FLAGS) "run $(notdir $(basename $<)) --backend flo --genHarness --compile --test $(CHISEL_FLAGS)"

%.v: %.scala
	$(SBT) $(SBT_FLAGS) "run $(notdir $(basename $<)) --genHarness --backend v $(CHISEL_FLAGS)"
