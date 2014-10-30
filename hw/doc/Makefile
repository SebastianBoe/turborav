PDFLATEX ?= pdflatex

RISCV_OPCODES = ../tools/riscv-opcodes

get-table:
	# TODO create enviroment variables makes paths less complicated.
	make -C $(RISCV_OPCODES) instr-table.tex
	cp $(RISCV_OPCODES)/instr-table.tex .


all: get-table
	$(PDFLATEX) opcodes.tex

clean:
	rm -f instr-table.texi opcodes.pdf *.aux *.dvi *.log
