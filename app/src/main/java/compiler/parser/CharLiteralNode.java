package compiler.parser;

import compiler.lexer.Token;

public class CharLiteralNode extends ExprNode {
    public final char value;
    public final Token token;
    public CharLiteralNode(char value, Token token) {
        this.value = value;
        this.token = token;
    }
    @Override
    public String toString() {
        return String.format("""
            {"node_type":"CharLiteralNode","value":"%c"}""",
            value
        );
    }
    @Override
    public int getPos() {
        return token.getPos();
    }
    @Override
    public <R> R accept(AstVisitor<R> visitor) { return visitor.visit(this); }
}
