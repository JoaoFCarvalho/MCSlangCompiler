# Lexer

A análise léxica, feita pela classe `Lexer`, é a primeira etapa do compilador. Ela serve para transformar o texto do código-fonte (uma sequência de caracteres) em uma lista de "tokens", que são unidades sintáticas básicas como palavras-chave, identificadores, operadores, literais, etc. Esses tokens são usados pelas próximas etapas do compilador para entender e processar o código.

### Para que serve a análise léxica?

- **Quebra o texto em partes compreensíveis:** Em vez de trabalhar com texto puro, o compilador passa a trabalhar com tokens, facilitando a análise e detecção de erros.
- **Identifica erros básicos:** Caracteres ou sequências inválidas já são detectados aqui.
- **Classifica cada pedaço do código:** Por exemplo, diferencia entre uma palavra-chave (`if`), um identificador (`x`), um número (`42`), um operador (`+`), etc.

### Como funciona neste projeto?

1. **Entrada:** O método principal é `Lexer.tokenize`, que recebe o código-fonte como uma string.
2. **Processamento:** O método `run()` percorre o texto caractere por caractere, pulando espaços em branco e identificando o tipo de cada sequência:
  - **Palavras e identificadores:** Se começa com letra ou `_`, chama `scanWord()`, que reconhece palavras-chave (como `var`, `if`, `while`), tipos (`int`, `float`), booleanos (`true`, `false`) ou nomes de variáveis/funções (identificadores).
  - **Números:** Se começa com dígito ou `.`, chama `scanNumber()`, que reconhece inteiros e floats, inclusive binários, octais e hexadecimais.
  - **Strings:** Se começa com `"`, chama `scanString()`, que lê até o próximo `"`, tratando escapes como `\n`, `\t`, etc.
  - **Caracteres:** Se começa com `'`, chama `scanCharLiteral()`, que lê um caractere (com ou sem escape).
  - **Comentários:** Se começa com `//` ou `/*`, chama `scanComment()` ou `scanMultiLineComment()`, ignorando o conteúdo.
  - **Símbolos:** Para operadores e pontuação (como `+`, `==`, `;`, etc.), chama `scanSymbol()`, que compara com uma lista de símbolos conhecidos.
  - **Fim de arquivo:** Quando chega ao final, adiciona um token especial `EOF`.
3. Saída: Retorna uma lista de objetos `Token`, cada um com tipo, texto original e posição no código.
4. Erros: Se encontrar algo desconhecido, gera um token de erro. O método `tokenize` verifica se há erros e lança uma exceção se necessário.

### Exemplo

Para o código:

```
var x: int = 42;
```

A análise léxica produziria tokens como:

- KW_VAR (palavra-chave `var`)
- IDENT (identificador `x`)
- SY_COLON (`:`)
- TY_INT (tipo `int`)
- SY_ASSIGN (`=`)
- LT_INT (literal inteiro `42`)
- SY_SEMICOLON (`;`)
- EOF (fim do arquivo)

Assim, a análise léxica prepara o código para as próximas etapas do compilador, tornando o processamento mais estruturado e seguro.

# Parser

A etapa de parsing (análise sintática) é a segunda fase do compilador. Ela recebe a lista de tokens produzida pelo lexer e constrói uma estrutura de dados chamada Árvore de Sintaxe Abstrata (AST, do inglês Abstract Syntax Tree). Essa árvore representa a estrutura lógica do programa, mostrando como os diferentes elementos do código se relacionam.

### Para que serve o parsing?

- **Entender a estrutura do código:** O parser verifica se a sequência de tokens faz sentido de acordo com as regras da linguagem (a gramática). Por exemplo, garante que expressões, comandos, funções, laços, etc., estejam escritos corretamente.
- **Construir a AST:** A árvore de sintaxe abstrata facilita as próximas etapas do compilador, como análise semântica e geração de código, pois organiza o programa de forma hierárquica e estruturada.
- **Detectar erros de sintaxe:** Se o código não segue as regras da linguagem, o parser aponta o erro e interrompe o processo.

### Como funciona neste projeto?

1. **Entrada:** O parser recebe a lista de tokens do lexer.
2. **Processamento:** O método principal percorre os tokens e, seguindo as regras da gramática da linguagem, monta a AST. Cada construção da linguagem (declaração de variável, função, laço, expressão, etc.) tem um método específico para ser reconhecida e transformada em um nó da árvore.
   - Por exemplo, para um comando `if`, o parser espera encontrar a palavra-chave `if`, seguida de parênteses com uma expressão, e depois um bloco de código. Se a estrutura não for respeitada, um erro é gerado.
   - O parser utiliza métodos recursivos para lidar com estruturas aninhadas, como expressões matemáticas e blocos de código.
   - A gramática da linguagem está implementada em métodos como `parseStatement`, `parseExpr`, `parseFuncDecl`, etc., que refletem as regras de formação dos comandos e expressões.
3. **Saída:** O resultado é uma AST, composta por objetos que representam cada parte do programa (nós para variáveis, funções, comandos, expressões, etc.).

### Exemplo

Para o código:

```
if (x > 0) {
    print(x);
} else {
    print(-x);
}
```

O parser gera uma árvore com um nó principal representando o comando `if`, que possui como filhos a condição (`x > 0`), o bloco do `then` (`print(x);`) e o bloco do `else` (`print(-x);`).

### Padrão de Projeto Visitor

Neste projeto, a AST utiliza o padrão de projeto **Visitor**. Esse padrão permite separar operações (como análise semântica ou geração de código) da estrutura dos nós da árvore. Cada nó da AST possui um método `accept(visitor)`, que recebe um objeto visitor. O visitor tem métodos específicos para cada tipo de nó (por exemplo, `visit(FuncDeclNode)`, `visit(AssignmentNode)`, etc.).

**Vantagens do Visitor:**
- Permite adicionar novas operações sobre a AST sem modificar as classes dos nós.
- Facilita a manutenção e a organização do código.
- É usado em várias etapas do compilador, como análise semântica e geração de código.

**Exemplo simplificado:**

```java
// Em cada nó da AST:
public <R> R accept(AstVisitor<R> visitor) {
    return visitor.visit(this);
}

// No visitor:
public R visit(FuncDeclNode node) { ... }
public R visit(AssignmentNode node) { ... }
// etc.
```

Assim, o parser transforma o código em uma estrutura de árvore, e o padrão visitor permite que diferentes operações sejam aplicadas sobre essa árvore de forma flexível e organizada.

# Error

O pacote `error` do projeto é responsável por acumular e gerenciar os erros encontrados durante as etapas do compilador (como análise léxica, sintática e semântica). Em vez de interromper o processamento ao encontrar o primeiro erro, o compilador pode continuar analisando o código e registrar todos os problemas encontrados em uma lista de erros.

Ao final do processo, todos os erros acumulados são exibidos juntos para o usuário. Isso facilita a identificação e correção de múltiplos problemas de uma só vez, tornando o processo de desenvolvimento mais eficiente.

Por exemplo, durante a análise léxica, se houver vários símbolos desconhecidos, todos eles podem ser reportados em uma única execução, em vez de exigir várias rodadas de correção e compilação.

Esse mecanismo é implementado por meio de uma classe (como `ErrorList`) que armazena as mensagens de erro e suas posições no código, e pode lançar uma exceção contendo todos os erros ao final da análise.

