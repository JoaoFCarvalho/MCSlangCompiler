package compiler.parser;

import compiler.lexer.Token;

public class ForNode extends StmtNode {
    public final AstNode init; // VarDeclNode or ExprNode
    public final AstNode cond; // ExprNode or null
    public final AstNode step; // ExprNode or null
    public final StmtNode body;
    public final Token startToken; // for error reporting
    public ForNode(AstNode init, AstNode cond, AstNode step, StmtNode body, Token startToken) {
        this.init = init;
        this.cond = cond;
        this.step = step;
        this.body = body;
        this.startToken = startToken;
    }
    @Override
    public String toString() {
        return String.format("""
            {"node_type":"ForNode","init":%s,"cond":%s,"step":%s,"body":%s}""",
            init,
            cond != null ? cond.toString() : "null",
            step != null ? step.toString() : "null",
            body
        );
    }
    
    @Override
    public int getPos() {
        return startToken.getPos();
    }

    @Override
    public <R> R accept(AstVisitor<R> visitor) { return visitor.visit(this); }
}
