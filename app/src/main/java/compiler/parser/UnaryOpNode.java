package compiler.parser;

import compiler.lexer.Token;

public class UnaryOpNode extends ExprNode {
    public enum OpType {
        NOT, NEGATE,
    }
    public final OpType op;
    public final Token token;
    public final ExprNode operand;
    public UnaryOpNode(OpType op, Token token, ExprNode operand) {
        this.op = op;
        this.token = token;
        this.operand = operand;
    }
    @Override
    public String toString() {
        return String.format("""
            {"node_type":"UnaryOpNode","op":"%s","operand":%s}""",
            op.name().toLowerCase(),
            operand.toString()
        );
    }
    @Override
    public int getPos() { return token.getPos(); }
    @Override
    public <R> R accept(AstVisitor<R> visitor) { return visitor.visit(this); }
}
