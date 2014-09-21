{ module Main where import Tokenizer }

%name turborav
%tokentype { Token }
%error { parseError }
%token
  intLiteral { Int         $$ }
  '+'        { TOpPlus        }
  '-'        { TOpMinus       }
  '='        { TOpAssign      }
  ident   { TIdent   $$ }
%%
Stmts : Stmt { Stmt [$1] }
      | Stmts Stmt { $2 : $1 }

Stmt : ident '=' Exp { Stmt $1 $3 }

Exp : Exp '+' Exp { Plus  $1 $3 }
    | Exp '-' Exp { Minus $1 $3 }
    | intLiteral  { $1          }
    | ident    { Ident $1 }

{
parseError :: [Token] -> a
parseError _ = error "Parse error"

data Stmts = [Stmt]
           | Stmts : Stmt
           deriving (Show)

}
