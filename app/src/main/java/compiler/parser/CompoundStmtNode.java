package compiler.parser;

import java.util.*;

import compiler.lexer.Token;

public class CompoundStmtNode extends StmtNode {
    public final List<AstNode> statements;
    public final Token startToken;
    public CompoundStmtNode(List<AstNode> statements, Token startToken) {
        this.statements = statements;
        this.startToken = startToken;
    }
    public int getPos() { return startToken.getPos(); }
    @Override
    public String toString() {
        return String.format("""
            {"node_type":"CompoundStmtNode","statements":[%s]}""",
            String.join(
                ",",
                statements.stream().map(AstNode::toString).toList()
            )
        );
    }
    @Override
    public <R> R accept(AstVisitor<R> visitor) { return visitor.visit(this); }
}
