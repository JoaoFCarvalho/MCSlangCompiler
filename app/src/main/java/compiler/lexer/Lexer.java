package compiler.lexer;

import java.util.*;

import compiler.error.ErrorList;

public class Lexer {
    private final String input;
    private List<Token> tokens;
    private int pos = 0;

    public static List<Token> tokenize(String input) throws ErrorList {
        Lexer lexer = new Lexer(input);
        lexer.run();
        ErrorList errorList = new ErrorList();
        for (Token token : lexer.tokens) {
            if (token.getType() == TokenType.ERROR) {
                errorList.add(
                    String.format("Unrecognized token `%s`", token.getLexeme()),
                    token.getPos()
                );
            }
        }
        if (!errorList.isEmpty()) {
            throw errorList;
        }
        return lexer.tokens;
    }

    private Lexer(String input) {
        this.input = input;
    }

//// HELPER METHODS ////////////////////////////////////////////////////////////

    private char peek() {
        return pos < input.length() ? input.charAt(pos) : '\0';
    }

    private void skipWhitespace() {
        while (Character.isWhitespace(peek())) {
            pos++;
        }
    }

    private boolean match(String expected) {
        return input.startsWith(expected, pos);
    }

//// MAIN LOGIC ////////////////////////////////////////////////////////////////

    private void run() {
        tokens = new ArrayList<>();

        while (true) {
            skipWhitespace();
            char c = peek();

            if (c == '\0') {
                tokens.add(new Token(TokenType.EOF, "", pos));
                break;
            } else if (Character.isLetter(c) || c == '_') {
                tokens.add(scanWord());
            } else if (Character.isDigit(c) || c == '.') {
                tokens.add(scanNumber());
            } else if (c == '"') {
                tokens.add(scanString());
            } else if (c == '\'') {
                tokens.add(scanCharLiteral());
            } else if (match("//")) {
                scanComment();
            } else if (match("/*")) {
                scanMultiLineComment();
            } else {
                tokens.add(scanSymbol());
            }
        }
    }

    private Token scanWord() {
        int start = pos;
        while (Character.isLetterOrDigit(peek()) || peek() == '_') {
            pos++;
        }
        String lexeme = input.substring(start, pos);

        return switch (lexeme) {
            case "var"    -> new Token(TokenType.KW_VAR,    lexeme, start);
            case "func"   -> new Token(TokenType.KW_FUNC,   lexeme, start);
            case "if"     -> new Token(TokenType.KW_IF,     lexeme, start);
            case "else"   -> new Token(TokenType.KW_ELSE,   lexeme, start);
            case "while"  -> new Token(TokenType.KW_WHILE,  lexeme, start);
            case "for"    -> new Token(TokenType.KW_FOR,    lexeme, start);
            case "return" -> new Token(TokenType.KW_RETURN, lexeme, start);
            case "int"    -> new Token(TokenType.TY_INT,    lexeme, start);
            case "float"  -> new Token(TokenType.TY_FLOAT,  lexeme, start);
            case "char"   -> new Token(TokenType.TY_CHAR,   lexeme, start);
            case "string" -> new Token(TokenType.TY_STRING, lexeme, start);
            case "bool"   -> new Token(TokenType.TY_BOOL,   lexeme, start);

            case "true"  -> new TokenLiteral(TokenType.LT_BOOL, lexeme, true,  start);
            case "false" -> new TokenLiteral(TokenType.LT_BOOL, lexeme, false, start);

            default -> new Token(TokenType.IDENT, lexeme, start);
        };
    }

    private Token scanNumber() {
        int start = pos;

        while (true) {
            char c = peek();
            if (match("e-") || match("e+") || match("E-") || match("E+")) {
                pos += 2;
            } else if (Character.isDigit(c) || Character.isAlphabetic(c) || c == '.' || c == '_') {
                pos++;
            } else {
                break;
            }
        }

        String lexeme = input.substring(start, pos);

        // Regexes for number literals
        String floatRegex = "^(\\d+\\.\\d*([eE][+-]?\\d+)?|\\d+[eE][+-]?\\d+|\\.\\d+)$";
        String binaryRegex = "^0[bB][01_]+$";
        String octalRegex = "^0[oO][0-7_]+$";
        String hexRegex = "^0[xX][0-9a-fA-F_]+$";
        String intRegex = "^\\d+$";

        if (lexeme.matches(floatRegex)) {
            double value = Double.parseDouble(lexeme);
            return new TokenLiteral(TokenType.LT_FLOAT, lexeme, value, start);
        } else if (lexeme.matches(binaryRegex)
                || lexeme.matches(octalRegex)
                || lexeme.matches(hexRegex)
                || lexeme.matches(intRegex)) {
            long value = Long.parseLong(lexeme);
            return new TokenLiteral(TokenType.LT_INT, lexeme, value, start);
        }

        return new Token(TokenType.ERROR, lexeme, start);
    }

    private Token scanString() {
        int start = pos;
        pos++; // Skip opening quote

        StringBuilder sb = new StringBuilder();
        while (true) {
            char c = peek();
            if (c == '\0') {
                return new Token(TokenType.ERROR, input.substring(start), start);
            } else if (c == '"') {
                pos++; // Skip closing quote
                break;
            } else if (c == '\\') {
                pos++; // Skip escape character
                c = peek();
                c = switch (c) {
                    case 'n'  -> '\n';
                    case 't'  -> '\t';
                    case 'r'  -> '\r';
                    case 'b'  -> '\b';
                    case 'f'  -> '\f';
                    case '"'  -> '"';
                    case '\'' -> '\'';
                    case '\\' -> '\\';
                    default -> c; // Keep the character as is
                };
                sb.append(c);
            } else {
                sb.append(c);
            }
            pos++;
        }
        String lexeme = input.substring(start, pos);
        return new TokenLiteral(TokenType.LT_STRING, lexeme, sb.toString(), start);
    }

    private Token scanCharLiteral() {
        int start = pos;
        pos++;

        char c = peek();
        if (c == '\0') {
            return new Token(TokenType.ERROR, input.substring(start), start);
        } else if (c == '\\') {
            pos++; // Skip escape character
            c = peek();
            c = switch (c) {
                case 'n'  -> '\n';
                case 't'  -> '\t';
                case 'r'  -> '\r';
                case 'b'  -> '\b';
                case 'f'  -> '\f';
                case '"'  -> '"';
                case '\'' -> '\'';
                case '\\' -> '\\';
                default -> c; // Keep the character as is
            };
            pos++; // Skip the escaped character
        } else {
            pos++; // Skip the character
        }
        if (peek() != '\'') {
            return new Token(TokenType.ERROR, input.substring(start), start);
        }
        pos++; // Skip closing quote
        String lexeme = input.substring(start, pos);
        return new TokenLiteral(TokenType.LT_CHAR, lexeme, c, start);
    }

    private Token scanComment() {
        int start = pos;
        pos += 2; // Skip "//"

        while (peek() != '\n' && peek() != '\0') {
            pos++;
        }

        return new Token(TokenType.COMMENT, input.substring(start, pos), start);
    }

    private Token scanMultiLineComment() {
        int start = pos;
        pos += 2;

        while (true) {
            if (peek() == '\0') {
                return new Token(TokenType.ERROR, input.substring(start), start);
            } else if (match("*/")) {
                pos += 2;
                break;
            }
            pos++;
        }

        return new Token(TokenType.COMMENT, input.substring(start, pos), start);
    }

    private Token scanSymbol() {
        final String[] symbols = {
                "||", "&&", "==", "!=", ">=", "<=", "+=", "-=", "*=", "/=",
                ":", "=", ";", "(", ")", ",", "{", "}", ">", "<", "+", "-",
                "*", "/", "!"
        };
        final TokenType[] types = {
                TokenType.SY_OR, TokenType.SY_AND, TokenType.SY_EQ, TokenType.SY_NEQ,
                TokenType.SY_GTE, TokenType.SY_LTE, TokenType.SY_ADDASSIGN,
                TokenType.SY_SUBASSIGN, TokenType.SY_MULASSIGN, TokenType.SY_DIVASSIGN,
                TokenType.SY_COLON, TokenType.SY_ASSIGN, TokenType.SY_SEMICOLON,
                TokenType.SY_LPAREN, TokenType.SY_RPAREN, TokenType.SY_COMMA,
                TokenType.SY_LBRACE, TokenType.SY_RBRACE, TokenType.SY_GT,
                TokenType.SY_LT, TokenType.SY_ADD, TokenType.SY_SUB, TokenType.SY_MUL,
                TokenType.SY_DIV, TokenType.SY_NOT
        };
        for (int i = 0; i < symbols.length; i++) {
            if (match(symbols[i])) {
                Token token = new Token(types[i], symbols[i], pos);
                pos += symbols[i].length();
                return token;
            }
        }
        Token token = new Token(TokenType.ERROR, String.valueOf(peek()), pos);
        pos += 1; // Skip unrecognized character
        return token;
    }
}
