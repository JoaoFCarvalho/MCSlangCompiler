package compiler.parser;

public class ReturnNode extends StmtNode {
    public final ExprNode expr;
    public final int pos;
    public ReturnNode(ExprNode expr, int pos) {
        this.expr = expr;
        this.pos = pos;
    }
    @Override
    public int getPos() { return pos; }
    @Override
    public String toString() {
        return String.format("""
            {"node_type":"ReturnNode","expr":%s}""",
            expr != null ? expr.toString() : "null"
        );
    }
    @Override
    public <R> R accept(AstVisitor<R> visitor) { return visitor.visit(this); }
}
