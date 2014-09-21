{
module Main (main) where
}

%wrapper "basic"

$digit = 0-9
$alpha = [a-z]

tokens :-

  $white+				;
  "#".* 				;
  $digit+				{ \s -> Int (read s) }
  [\=\+\-]				{ \s -> Sym (head s) }
  [$alpha \_]+				{ \s -> Var s }

{

data Token =
	Sym Char	|
	Var String	|
	Int Int
	deriving (Eq,Show)

main = do
  s <- getContents
  print (alexScanTokens s)
}
