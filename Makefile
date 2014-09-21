Parser: Parser.hs Lexer.hs
	ghc $<

Parser.hs: Parser.y
	happy Parser.y

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
	LexerMain.hi \
	LexerMain.o \
	LexerMain \
	test/*.actual_result
