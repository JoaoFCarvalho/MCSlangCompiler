package compiler.parser;

import compiler.lexer.Token;

public class ConditionalNode extends StmtNode {
    public final ExprNode condition;
    public final StmtNode thenBranch;
    public final StmtNode elseBranch; // can be null
    public final Token token; // for error reporting
    public ConditionalNode(ExprNode condition, StmtNode thenBranch, StmtNode elseBranch, Token token) {
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
        this.token = token;
    }
    @Override
    public String toString() {
        return String.format("""
            {"node_type":"ConditionalNode","condition":%s,"thenBranch":%s,"elseBranch":%s}""",
            condition,
            thenBranch,
            elseBranch != null ? elseBranch.toString() : "null"
        );
    }
    
    @Override
    public int getPos() {
        return token.getPos();
    }

    @Override
    public <R> R accept(AstVisitor<R> visitor) { return visitor.visit(this); }
}
