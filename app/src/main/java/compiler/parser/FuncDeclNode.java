package compiler.parser;

import java.util.*;

import compiler.lexer.Token;

public class FuncDeclNode extends DeclNode {
    public final Token name;
    public final List<ParamNode> params;
    public final TypeNode returnType; // can be null if not specified
    public final CompoundStmtNode body;
    public FuncDeclNode(Token name, List<ParamNode> params, TypeNode returnType, CompoundStmtNode body) {
        this.name = name;
        this.params = params;
        this.returnType = returnType;
        this.body = body;
    }
    public int getPos() { return name.getPos(); }
    @Override
    public String toString() {
        return String.format("""
            {"node_type":"FuncDeclNode","name":"%s","params":[%s],"returnType":%s,"body":%s}""",
            name.getLexeme(),
            String.join(
                ",",
                params.stream().map(ParamNode::toString).toList()
            ),
            returnType != null ? returnType.toString() : "null",
            body
        );
    }
    @Override
    public <R> R accept(AstVisitor<R> visitor) { return visitor.visit(this); }
}
