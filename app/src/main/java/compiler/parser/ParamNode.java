package compiler.parser;

import compiler.lexer.Token;

public class ParamNode extends AstNode {
    public final Token name;
    public final TypeNode type;
    public ParamNode(Token name, TypeNode type) {
        this.name = name;
        this.type = type;
    }
    public int getPos() { return name.getPos(); }
    @Override
    public String toString() {
        return String.format("""
            {"node_type":"ParamNode","name":"%s","type":%s}""",
            name.getLexeme(),
            type
        );
    }
    @Override
    public <R> R accept(AstVisitor<R> visitor) { return visitor.visit(this); }
}
