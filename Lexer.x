{
module Lexer where
}

%wrapper "basic"

$digit = 0-9
$alpha = [a-z]

tokens :-

  $white+				;
  "#".* 				;
  $digit+				{ \s -> Int (read s) }
  [\+]				        { \s -> TOpPlus }
  [\-]				        { \s -> TOpMinus }
  [\=]                                  { \s -> TOpAssign }
  [$alpha \_]+				{ \s -> TIdent s }
{
data Token =
	Int Int          |
        TOpPlus          |
        TOpMinus         |
        TOpAssign        |
	TIdent String 
	deriving (Eq,Show)
}
