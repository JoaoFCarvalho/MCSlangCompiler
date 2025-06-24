package compiler.parser;

import compiler.lexer.Token;

public class VarDeclNode extends DeclNode {
    public final Token name;
    public final TypeNode type;
    public final ExprNode expr; // can be null if no initializer
    public VarDeclNode(Token name, TypeNode type, ExprNode expr) {
        this.name = name;
        this.type = type;
        this.expr = expr;
    }
    @Override
    public String toString() {
        return String.format("""
            {"node_type":"VarDeclNode","name":"%s","type":%s,"expr":%s}""",
            name.getLexeme(),
            type != null ? type.toString() : "null",
            expr != null ? expr.toString() : "null"
        );
    }
    @Override
    public int getPos() { return name.getPos(); }
    @Override
    public <R> R accept(AstVisitor<R> visitor) { return visitor.visit(this); }
}