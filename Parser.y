-- Synt.y -*- mode: haskell -*-
{ 
module Parser where 
import Lexer 
}

%name turborav
%tokentype { Token }
%error { parseError }
%token
  intLiteral { Int         $$ }
  '+'        { TOpPlus        }
  '-'        { TOpMinus       }
  '='        { TOpAssign      }
  ident   { TIdent   $$ }
%left '+' '-'
%%
Stmts :: {[Stmt]}
         : {[]}
         | Stmt Stmts { $1 : $2 }

Stmt : ident '=' Exp { Stmt $1 $3 }

Exp : Exp '+' Exp { Plus  $1 $3 }
    | Exp '-' Exp { Minus $1 $3 }
    | intLiteral  { $1          }
    | ident       { $1          }

{
parseError :: [Token] -> a
parseError _ = error "Parse error"

-- data Stmts = [Stmt]
--            | Stmt:Stmts
--            deriving (Show)

data Stmt = Stmt String Exp
          deriving (Show)

data Exp = Plus  Exp Exp
         | Minus Exp Exp
         | Int 
         | String String
         deriving (Show)

main = getContents >>= print . turborav . lexer
}

