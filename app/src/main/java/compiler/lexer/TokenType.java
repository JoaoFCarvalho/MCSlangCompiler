package compiler.lexer;

public enum TokenType {
    // Keywords
    KW_VAR, KW_FUNC, KW_IF, KW_ELSE,
    KW_WHILE, KW_FOR, KW_RETURN,
    // Types
    TY_INT, TY_FLOAT, TY_STRING,
    TY_CHAR, TY_BOOL,
    // Literals
    LT_INT, LT_FLOAT, LT_STRING,
    LT_CHAR, LT_BOOL,
    // Symbols
    SY_ADD, SY_SUB, SY_MUL, SY_DIV,
    SY_ADDASSIGN, SY_SUBASSIGN,
    SY_MULASSIGN, SY_DIVASSIGN,
    SY_LTE, SY_GTE, SY_GT, SY_LT,
    SY_EQ, SY_NEQ, SY_AND, SY_OR,
    SY_NOT, SY_ASSIGN, SY_SEMICOLON,
    SY_COMMA, SY_LPAREN, SY_RPAREN,
    SY_LBRACE, SY_RBRACE, SY_COLON,
    // Identifiers
    IDENT,
    // Misc
    EOF, ERROR, COMMENT,
}


// : = ; ( ) , { } || && == != >= <= > < + - * / !