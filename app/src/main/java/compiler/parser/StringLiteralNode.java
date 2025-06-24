package compiler.parser;

import compiler.lexer.Token;

public class StringLiteralNode extends ExprNode {
    public final String value;
    public final Token token;
    public StringLiteralNode(String value, Token token) {
        this.value = value;
        this.token = token;
    }
    @Override
    public String toString() {
        return String.format("""
            {"node_type":"StringLiteralNode","value":"%s"}""",
            value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
            .replace("\b", "\\b")
            .replace("\f", "\\f")
        );
    }
    @Override
    public int getPos() { return token.getPos(); }
    @Override
    public <R> R accept(AstVisitor<R> visitor) { return visitor.visit(this); }
}
