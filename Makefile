lexer: lexer.hs
	ghc $<

lexer.hs: lexer.x
	alex $<

clean:
	rm -f \
	lexer \
	lexer.hi \
	lexer.hs \
	lexer.o \
	test/*.actual_result
