package compiler.parser;

import compiler.lexer.Token;

public class AssignmentNode extends StmtNode {
    public enum OpType {
        ASSIGN, ADD_ASSIGN, SUB_ASSIGN, MUL_ASSIGN, DIV_ASSIGN
    }
    public final Token name;
    public final OpType op; // =, +=, -=, *=, /=
    public final ExprNode expr;
    public AssignmentNode(Token name, OpType op, ExprNode expr) {
        this.name = name;
        this.op = op;
        this.expr = expr;
    }
    @Override
    public String toString() {
        return String.format("""
            {"node_type":"AssignmentNode","name":"%s","op":"%s","expr":%s}""",
            name.getLexeme(), op.name().toLowerCase(), expr
        );
    }
    @Override
    public <R> R accept(AstVisitor<R> visitor) { return visitor.visit(this); }
    @Override
    public int getPos() { return name.getPos(); }
}
