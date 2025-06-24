# MACSLang Compiler

## Descrição
Este projeto é um compilador para a linguagem MACSLang, desenvolvido em Java utilizando Gradle e JUnit 5 para testes. O compilador é dividido em módulos para cada etapa da compilação:

- [x] **Analisador Léxico**
- [x] **Analisador Sintático**
- [ ] **Analisador Semântico**
- [ ] **Gerador de Código**

Cada etapa está implementada em um pacote separado.

## Gramática da Linguagem

```
Root =
    | Decls

Decls =
    | Decl Decls
    | ε

Decl =
    | VarDecl
    | FuncDecl

VarDecl =
    | "var" IDENT ":" Type "=" Expr ";"
    | "var" IDENT ":" Type ";"

FuncDecl =
    | "func" IDENT "(" Params ")" ":" Type CompoundStmt
    | "func" IDENT "(" Params ")" CompoundStmt

---

Type =
    | "int"
    | "float"
    | "string"
    | "char"
    | "bool"

Params =
    | IDENT ":" Type "," Params
    | IDENT ":" Type
    | ε

---

CompoundStmt =
    | "{" Statements "}"

Statements =
    | VarDecl Statements
    | Statement Statements
    | ε

Statement =
    | Assignment ";"
    | Conditional
    | Loop
    | Return
    | CompoundStmt
    | Expr ";"
    | ";"

---

Assignment =
    | IDENT "=" Expr
    | IDENT "+=" Expr
    | IDENT "-=" Expr
    | IDENT "*=" Expr
    | IDENT "/=" Expr

Conditional =
    | "if" "(" Expr ")" Statement "else" Statement
    | "if" "(" Expr ")" Statement

ForInit =
    | Assignment ";"
    | VarDecl
    | Expr ";"
    | ";"

ForInc =
    | Assignment
    | Expr
    | ε

Loop =
    | "while" "(" Expr ")" Statement
    | "for" "(" ForInit Expr ";" ForInc ")" Statement

Return =
    | "return" Expr ";"

---

FnCall =
    | IDENT "(" ArgsList ")"

ArgsList =
    | Expr "," ArgsList
    | Expr
    | ε

Expr =
    | Expr "||" Expr2
    | Expr2

Expr2 =
    | Expr2 "&&" Expr3
    | Expr3

Expr3 =
    | Expr4 "==" Expr4
    | Expr4 "!=" Expr4
    | Expr4 ">=" Expr4
    | Expr4 "<=" Expr4
    | Expr4 ">" Expr4
    | Expr4 "<" Expr4
    | Expr4

Expr4 =
    | Expr4 "+" Expr5
    | Expr4 "-" Expr5
    | Expr5

Expr5 =
    | Expr5 "*" Expr6
    | Expr5 "/" Expr6
    | Expr6

Expr6 =
    | "!" Expr7
    | "-" Expr7
    | Expr7

Expr7 =
    | FnCall
    | "(" Expr ")"
    | IDENT
    | LT_INT
    | LT_FLOAT
    | LT_STRING
    | LT_CHAR
    | LT_BOOL

---

LT_INT    = /\d+|0[bB][01]+|0[oO][0-7]+|0[xX][0-9a-fA-F]+/
LT_FLOAT  = /\.\d+|\d+\.\d*(e-?\d+)?/
LT_CHAR   = /'\\.'|'[^']'/
LT_STRING = /"(\\.|[^"])*"/
LT_BOOL   = /true|false/
```

Símbolos da linguagem:
```
: = ; ( ) , { } || && == != >= <= += -= *= /= > < + - * / !
```

Palavras reservadas:
```
var func if else while for return
int float string char bool
```

## Exemplos

### Declaração de Variáveis
```
var <nome>: <tipo> = <valor opcional>;
```
Os tipos suportados são:
- `int`    = Inteiros
- `float`  = Números de ponto flutuante
- `char`   = Caracteres
- `bool`   = Booleano (true ou false)
- `string` = Cadeia de caracteres

### Entrada e Saída
```
print(<expressão>);  // Exibe no console
input(<variável>);   // Lê valor do usuário e armazena na variável
```
### Estruturas Condicionais
```
if (<condição>) {
    // bloco de código
} else {
    // bloco alternativo
}
```
### Laços de Repetição
- `while`
```
while (<condição>) {
    // bloco de código
}
```
- `for`
```
for (<inicialização>; <condição>; <incremento>) {
    // bloco de código
}
```
- Funções
```
func <nome>(<parametros>): <tipo_retorno> {
    // bloco de código
    return <valor>;
}
```

- Código de exemplo
```
var global: string = "this is a global variable!\n";

func test(a: float, b: float): bool {
    while (a < 1e-5) a -= b;
    for (var i: int = 0; i < 10; i += 1) {
        print("iter: ", i);
    }
    input(b);
    if (a * b > 100.) {
        a = -a;
    } else {
        b *= -1;
    }
    return a > b && a <= 2 * b;
}
```

## Como compilar e testar

### Linux
Para compilar o projeto:
```
./gradlew build
```

Para rodar os testes:
```
./gradlew test
```

### Windows
Para compilar o projeto:
```
gradlew.bat build
```

Para rodar os testes:
```
gradlew.bat test
```
