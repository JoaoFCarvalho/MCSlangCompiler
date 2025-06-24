package compiler.parser;

import compiler.lexer.Token;

public class WhileNode extends StmtNode {
    public final ExprNode condition;
    public final StmtNode body;
    public final Token token;
    public WhileNode(ExprNode condition, StmtNode body, Token token) {
        this.condition = condition;
        this.body = body;
        this.token = token;
    }
    @Override
    public String toString() {
        return String.format("""
            {"node_type":"WhileNode","condition":%s,"body":%s}""",
            condition,
            body
        );
    }
    @Override
    public int getPos() { return token.getPos(); }
    public <R> R accept(AstVisitor<R> visitor) { return visitor.visit(this); }
}
