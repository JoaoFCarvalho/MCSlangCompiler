package compiler.parser;

public interface AstVisitor<R> {
    R visit(ProgramNode node);
    R visit(VarDeclNode node);
    R visit(FuncDeclNode node);
    R visit(TypeNode node);
    R visit(ParamNode node);
    R visit(CompoundStmtNode node);
    R visit(AssignmentNode node);
    R visit(ReturnNode node);
    R visit(ConditionalNode node);
    R visit(WhileNode node);
    R visit(ForNode node);
    R visit(IdentExprNode node);
    R visit(IntLiteralNode node);
    R visit(FloatLiteralNode node);
    R visit(StringLiteralNode node);
    R visit(CharLiteralNode node);
    R visit(BoolLiteralNode node);
    R visit(BinaryOpNode node);
    R visit(UnaryOpNode node);
    R visit(FnCallNode node);
}
