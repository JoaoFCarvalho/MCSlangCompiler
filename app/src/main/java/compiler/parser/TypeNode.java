package compiler.parser;

public enum TypeNode {
    INT,
    FLOAT,
    CHAR,
    STRING,
    BOOL,
    VOID;

    @Override
    public String toString() {
        return String.format("""
            "%s\"""",
            this.name().toLowerCase()
        );
    }

    public <R> R accept(AstVisitor<R> visitor) { return visitor.visit(this); }
}
