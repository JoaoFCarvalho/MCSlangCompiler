package compiler.parser;

import compiler.lexer.Token;

public class IdentExprNode extends ExprNode {
    public final Token name;
    public IdentExprNode(Token name) {
        this.name = name;
    }
    @Override
    public String toString() {
        return String.format("""
            {"node_type":"IdentExprNode","name":"%s"}""",
            name.getLexeme()
        );
    }
    @Override
    public int getPos() {
        return name.getPos();
    }
    @Override
    public <R> R accept(AstVisitor<R> visitor) { return visitor.visit(this); }
}
