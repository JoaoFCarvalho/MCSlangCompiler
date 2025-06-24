package compiler.parser;

import compiler.lexer.Token;

public class IntLiteralNode extends ExprNode {
    public final long value;
    public final Token token; // for error reporting
    public IntLiteralNode(long value, Token token) {
        this.value = value;
        this.token = token;
    }
    @Override
    public String toString() {
        return String.format("""
            {"node_type":"IntLiteralNode","value":%s}""",
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
