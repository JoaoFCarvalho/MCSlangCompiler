package compiler.parser;

public abstract class AstNode {
    // Base class for all AST nodes
    public abstract <R> R accept(AstVisitor<R> visitor);
    public abstract int getPos(); // { return -1; } // Default implementation, override in concrete nodes
}
    