Tokens: Tokens.hs
	ghc $<

Tokens.hs: Tokens.x
	alex $<

clean:
	rm Tokens Tokens.hi Tokens.hs Tokens.o
