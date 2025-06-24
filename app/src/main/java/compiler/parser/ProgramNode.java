package compiler.parser;

import java.util.*;

public class ProgramNode extends AstNode {
    public final List<DeclNode> decls;
    public ProgramNode(List<DeclNode> decls) {
        this.decls = decls;
    }
    public int getPos() { return 0; }
    @Override
    public String toString() {
        return String.format("""
            {"node_type":"ProgramNode","decls":[%s]}""",
            String.join(
                ",",
                decls.stream().map(DeclNode::toString).toList()
            )
        );
    }
    @Override
    public <R> R accept(AstVisitor<R> visitor) { return visitor.visit(this); }
}
