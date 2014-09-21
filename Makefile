regression_test:
	./Tokens < test/sanity.tr > test/sanity.actual_result
	cmp test/sanity.expected_result test/sanity.actual_result

Tokens: Tokens.hs
	ghc $<

Tokens.hs: Tokens.x
	alex $<

clean:
	rm -f \
	Tokens \
	Tokens.hi \
	Tokens.hs \
	Tokens.o \
	test/*.actual_result
