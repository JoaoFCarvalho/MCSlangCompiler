package compiler.parser;

import compiler.lexer.Token;

public class BinaryOpNode extends ExprNode {
    public enum OpType {
        ADD, SUB, MUL, DIV, AND, OR,
        EQ, NEQ, GT, LT, GTE, LTE
    }
    public final OpType op;
    public final Token token; // The operator token, if needed for error reporting
    public final ExprNode lhs;
    public final ExprNode rhs;
    public BinaryOpNode(OpType op, Token token, ExprNode lhs, ExprNode rhs) {
        this.op = op;
        this.token = token;
        this.lhs = lhs;
        this.rhs = rhs;
    }
    @Override
    public String toString() {
        return String.format("""
            {"node_type":"BinaryOpNode","op":"%s","lhs":%s,"rhs":%s}""",
            op.name().toLowerCase(),
            lhs.toString(),
            rhs.toString()
        );
    }
    @Override
    public int getPos() {
        return token.getPos();
    }
    @Override
    public <R> R accept(AstVisitor<R> visitor) { return visitor.visit(this); }
}
