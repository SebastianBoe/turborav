Parser.hs: Parser.y
	happy Parser.hs

Parser: Parser.hs
	ghc $<

Lexer: Lexer.hs
	ghc $<

Lexer.hs: Lexer.x
	alex $<

clean:
	@rm -f \
	Lexer \
	Lexer.hi \
	Lexer.hs \
	Lexer.o \
	Parser.info \
	Parser.hs \
	test/*.actual_result
