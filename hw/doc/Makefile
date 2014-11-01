RISCV_OPCODES = ../tools/riscv-opcodes

all:
	make -C $(RISCV_OPCODES) instr-table.tex
	cp $(RISCV_OPCODES)/instr-table.tex .
	pdflatex opcodes.tex

clean:
	rm -f instr-table.texi opcodes.pdf *.aux *.dvi *.log
