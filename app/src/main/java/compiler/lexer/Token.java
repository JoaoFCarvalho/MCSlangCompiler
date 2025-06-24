package compiler.lexer;

public class Token {
    private TokenType type;
    private String lexeme;
    private int pos;

    public Token(TokenType type, String lexeme, int pos) {
        this.type = type;
        this.lexeme = lexeme;
        this.pos = pos;
    }

    public TokenType getType() {
        return type;
    }

    public String getLexeme() {
        return lexeme;
    }

    public int getPos() {
        return pos;
    }

    public String toString() {
        return String.format("[%s '%s']", type, lexeme);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Token)) return false;
        Token other = (Token) obj;
        return type == other.type && lexeme.equals(other.lexeme) && pos == other.pos;
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + lexeme.hashCode();
        result = 31 * result + pos;
        return result;
    }
}
