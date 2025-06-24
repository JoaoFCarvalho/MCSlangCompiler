package compiler.parser;

import compiler.lexer.Token;

public class BoolLiteralNode extends ExprNode {
    public final boolean value;
    public final Token token;
    public BoolLiteralNode(boolean value, Token token) {
        this.value = value;
        this.token = token;
    }
    @Override
    public String toString() {
        return String.format("""
            {"node_type":"BoolLiteralNode","value":%s}""",
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
