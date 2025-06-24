package compiler.lexer;

public class TokenLiteral extends Token {
    private Object value;

    public TokenLiteral(TokenType type, String lexeme, Object value, int pos) {
        super(type, lexeme, pos);
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public String toString() {
        return String.format("[%s (%s)]", getType(), value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof TokenLiteral)) return false;
        TokenLiteral other = (TokenLiteral) obj;
        return super.equals(other) && value.equals(other.value);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }
}
