package compiler.parser;

import java.util.*;

import compiler.lexer.Token;

public class FnCallNode extends ExprNode {
    public final Token name;
    public final List<ExprNode> args;
    public FnCallNode(Token name, List<ExprNode> args) {
        this.name = name;
        this.args = args;
    }
    @Override
    public String toString() {
        return String.format("""
            {"node_type":"FnCallNode","name":"%s","args":[%s]}""",
            name.getLexeme(),
            String.join(
                ",",
                args.stream().map(ExprNode::toString).toList()
            )
        );
    }
    @Override
    public int getPos() { return name.getPos(); }
    @Override
    public <R> R accept(AstVisitor<R> visitor) { return visitor.visit(this); }
}
