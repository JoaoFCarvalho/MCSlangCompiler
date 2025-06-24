package compiler.parser;

import compiler.lexer.Token;

public class FloatLiteralNode extends ExprNode {
    public final double value;
    public final Token token; // for error reporting
    public FloatLiteralNode(double value, Token token) {
        this.value = value;
        this.token = token;
    }
    @Override
    public String toString() {
        return String.format("""
            {"node_type":"FloatLiteralNode","value":%s}""",
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
