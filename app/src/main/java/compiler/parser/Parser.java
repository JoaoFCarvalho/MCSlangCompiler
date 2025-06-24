package compiler.parser;

import compiler.error.ErrorList;
import compiler.lexer.*;
import java.util.*;

public class Parser {
    private final List<Token> tokens;
    private int pos = 0;

    public static ProgramNode parse(List<Token> tokens) throws ErrorList {
        Parser parser = new Parser(tokens);
        return parser.parseRoot();
    }

    private Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

//// HELPER METHODS ////////////////////////////////////////////////////////////

    private Token peek() {
        return pos < tokens.size() ? tokens.get(pos) : tokens.get(tokens.size() - 1);
    }

    private Token advance() {
        if (pos < tokens.size())
            pos++;
        return tokens.get(pos - 1);
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (peek().getType() == type) {
                advance();
                return true;
            }
        }
        return false;
    }

    private boolean check(TokenType type) {
        return peek().getType() == type;
    }

    private Token expect(TokenType type) {
        if (check(type))
            return advance();
        Token token = peek();
        throw new ErrorList(String.format(
            "Expected %s, got `%s`", type.name().toUpperCase(), token.getLexeme()
        ), token.getPos());
    }

//// MAIN LOGIC ////////////////////////////////////////////////////////////////

    // Root = Decls
    private ProgramNode parseRoot() {
        List<DeclNode> decls = parseDecls();
        expect(TokenType.EOF);
        return new ProgramNode(decls);
    }

    // Decls = Decl Decls | ε
    private List<DeclNode> parseDecls() {
        List<DeclNode> decls = new ArrayList<>();
        while (check(TokenType.KW_VAR) || check(TokenType.KW_FUNC)) {
            decls.add(parseDecl());
        }
        return decls;
    }

    // Decl = VarDecl | FuncDecl
    private DeclNode parseDecl() {
        if (check(TokenType.KW_VAR))
            return parseVarDecl();
        if (check(TokenType.KW_FUNC))
            return parseFuncDecl();
        Token token = peek();
        throw new ErrorList(String.format(
            "Expected declaration, got `%s`", token.getLexeme()
        ), token.getPos());
    }

    // VarDecl = "var" IDENT ":" Type "=" Expr ";"
    //         | "var" IDENT ":" Type ";"
    private VarDeclNode parseVarDecl() {
        expect(TokenType.KW_VAR);
        Token name = expect(TokenType.IDENT);
        expect(TokenType.SY_COLON);
        TypeNode type = parseType();
        ExprNode expr = null;
        if (match(TokenType.SY_ASSIGN)) {
            expr = parseExpr();
        }
        expect(TokenType.SY_SEMICOLON);
        return new VarDeclNode(name, type, expr);
    }

    // FuncDecl = "func" IDENT "(" Params ")" ":" Type CompoundStmt | "func" IDENT
    // "(" Params ")" CompoundStmt
    private FuncDeclNode parseFuncDecl() {
        expect(TokenType.KW_FUNC);
        Token name = expect(TokenType.IDENT);
        expect(TokenType.SY_LPAREN);
        List<ParamNode> params = parseParams();
        expect(TokenType.SY_RPAREN);
        TypeNode returnType = null;
        if (match(TokenType.SY_COLON)) {
            returnType = parseType();
        }
        CompoundStmtNode body = parseCompoundStmt();
        return new FuncDeclNode(name, params, returnType, body);
    }

    // Type = "int" | "float" | "string" | "char" | "bool"
    private TypeNode parseType() {
        Token t = advance();
        return switch (t.getType()) {
            case TokenType.TY_INT    -> TypeNode.INT;
            case TokenType.TY_FLOAT  -> TypeNode.FLOAT;
            case TokenType.TY_STRING -> TypeNode.STRING;
            case TokenType.TY_CHAR   -> TypeNode.CHAR;
            case TokenType.TY_BOOL   -> TypeNode.BOOL;
            default -> throw new ErrorList(String.format(
                "Expected type, got `%s`", t.getLexeme()
            ), t.getPos());
        };
    }

    // Params = IDENT ":" Type "," Params | IDENT ":" Type | ε
    private List<ParamNode> parseParams() {
        List<ParamNode> params = new ArrayList<>();
        if (check(TokenType.IDENT)) {
            Token name = expect(TokenType.IDENT);
            expect(TokenType.SY_COLON);
            TypeNode type = parseType();
            params.add(new ParamNode(name, type));
            while (match(TokenType.SY_COMMA)) {
                name = expect(TokenType.IDENT);
                expect(TokenType.SY_COLON);
                type = parseType();
                params.add(new ParamNode(name, type));
            }
        }
        return params;
    }

    // CompoundStmt = "{" Statements "}"
    private CompoundStmtNode parseCompoundStmt() {
        Token start = expect(TokenType.SY_LBRACE);
        List<AstNode> stmts = parseStatements();
        expect(TokenType.SY_RBRACE);
        return new CompoundStmtNode(stmts, start);
    }

    // Statements = VarDecl Statements | Statement Statements | ε
    private List<AstNode> parseStatements() {
        List<AstNode> stmts = new ArrayList<>();
        while (check(TokenType.KW_VAR) || isStatementStart()) {
            if (check(TokenType.KW_VAR)) {
                stmts.add(parseVarDecl());
            } else {
                stmts.add(parseStatement());
            }
        }
        return stmts;
    }

    private boolean isStatementStart() {
        return check(TokenType.IDENT)
            || check(TokenType.KW_IF)
            || check(TokenType.KW_WHILE)
            || check(TokenType.KW_FOR)
            || check(TokenType.KW_RETURN)
            || check(TokenType.SY_LBRACE)
            || check(TokenType.SY_SEMICOLON);
    }

    // Statement = Assignment | Conditional | Loop | Return | CompoundStmt | Expr ";" | ";"
    private StmtNode parseStatement() {
        while (check(TokenType.SY_SEMICOLON)) {
            advance(); // Skip empty statements
        }

        if (check(TokenType.IDENT)) {
            // Could be Assignment or FnCall (Expr)
            StmtNode stmt = lookaheadAssignOp()
                ? parseAssignment()
                : parseExpr();
            expect(TokenType.SY_SEMICOLON);
            return stmt;
        } else if (check(TokenType.KW_IF)) {
            return parseConditional();
        } else if (check(TokenType.KW_WHILE) || check(TokenType.KW_FOR)) {
            return parseLoop();
        } else if (check(TokenType.KW_RETURN)) {
            return parseReturn();
        } else if (check(TokenType.SY_LBRACE)) {
            return parseCompoundStmt();
        }
        Token token = peek();
        throw new ErrorList(String.format(
            "Expected statement, got `%s`", token.getLexeme()
        ), token.getPos());
    }

    private boolean lookaheadAssignOp() {
        if (check(TokenType.IDENT)) {
            if (pos + 1 < tokens.size()) {
                TokenType t = tokens.get(pos + 1).getType();
                return t == TokenType.SY_ASSIGN
                    || t == TokenType.SY_ADDASSIGN
                    || t == TokenType.SY_SUBASSIGN
                    || t == TokenType.SY_MULASSIGN
                    || t == TokenType.SY_DIVASSIGN;
            }
        }
        return false;
    }

    // Assignment = IDENT '=' Expr ';' | IDENT '+=' Expr ';' | ...
    private AssignmentNode parseAssignment() {
        Token name = expect(TokenType.IDENT);
        Token op = expect(tokens.get(pos).getType());
        ExprNode expr = parseExpr();
        AssignmentNode.OpType type = switch (op.getLexeme()) {
            case "="  -> AssignmentNode.OpType.ASSIGN;
            case "+=" -> AssignmentNode.OpType.ADD_ASSIGN;
            case "-=" -> AssignmentNode.OpType.SUB_ASSIGN;
            case "*=" -> AssignmentNode.OpType.MUL_ASSIGN;
            case "/=" -> AssignmentNode.OpType.DIV_ASSIGN;
            default -> throw new ErrorList(String.format(
                "Expected assignment operator, got `%s`", op.getLexeme()
            ), op.getPos());
        };
        return new AssignmentNode(name, type, expr);
    }

    // Conditional = 'if' '(' Expr ')' Statement 'else' Statement | 'if' '(' Expr
    // ')' Statement
    private ConditionalNode parseConditional() {
        Token start = expect(TokenType.KW_IF);
        expect(TokenType.SY_LPAREN);
        ExprNode cond = parseExpr();
        expect(TokenType.SY_RPAREN);
        StmtNode thenBranch = parseStatement();
        StmtNode elseBranch = null;
        if (match(TokenType.KW_ELSE)) {
            elseBranch = parseStatement();
        }
        return new ConditionalNode(cond, thenBranch, elseBranch, start);
    }

    // Loop = 'while' '(' Expr ')' Statement
    //      | 'for' '(' ForInit Expr ';' ForInc ')' Statement
    private StmtNode parseLoop() {
        Token start = peek();
        if (match(TokenType.KW_WHILE)) {
            expect(TokenType.SY_LPAREN);
            ExprNode cond = parseExpr();
            expect(TokenType.SY_RPAREN);
            StmtNode body = parseStatement();
            return new WhileNode(cond, body, start);
        } else if (match(TokenType.KW_FOR)) {
            expect(TokenType.SY_LPAREN);
            
            AstNode init;
            if (check(TokenType.KW_VAR)) {
                init = parseVarDecl();
            } else if (lookaheadAssignOp()) {
                init = parseAssignment();
                expect(TokenType.SY_SEMICOLON);
            } else {
                init = parseExpr();
                expect(TokenType.SY_SEMICOLON);
            }
            
            ExprNode cond = parseExpr();
            expect(TokenType.SY_SEMICOLON);
            
            StmtNode step;
            if (check(TokenType.SY_RPAREN)) {
                step = null;
            } else if (lookaheadAssignOp()) {
                step = parseAssignment();
            } else {
                step = parseExpr();
            }

            expect(TokenType.SY_RPAREN);
            StmtNode body = parseStatement();
            return new ForNode(init, cond, step, body, start);
        }
        Token token = peek();
        throw new ErrorList(String.format(
            "Expected loop statement, found `%s`", token.getLexeme()
        ), token.getPos());
    }

    // Return = 'return' Expr ';'
    private ReturnNode parseReturn() {
        Token ret = expect(TokenType.KW_RETURN);
        ExprNode expr = parseExpr();
        expect(TokenType.SY_SEMICOLON);
        return new ReturnNode(expr, ret.getPos());
    }

    // --- Expression Parsing ---
    // Expr = Expr '||' Expr2 | Expr2
    private ExprNode parseExpr() {
        ExprNode left = parseExpr2();
        Token opToken = peek();
        while (match(TokenType.SY_OR)) {
            ExprNode right = parseExpr2();
            left = new BinaryOpNode(BinaryOpNode.OpType.OR, opToken, left, right);
            opToken = peek();
        }
        return left;
    }

    // Expr2 = Expr2 '&&' Expr3 | Expr3
    private ExprNode parseExpr2() {
        ExprNode left = parseExpr3();
        Token opToken = peek();
        while (match(TokenType.SY_AND)) {
            ExprNode right = parseExpr3();
            left = new BinaryOpNode(BinaryOpNode.OpType.AND, opToken, left, right);
            opToken = peek();
        }
        return left;
    }

    // Expr3 = Expr4 '==' Expr4 | ...
    private ExprNode parseExpr3() {
        ExprNode left = parseExpr4();
        while (true) {
            Token opToken = peek();
            if (match(TokenType.SY_EQ)) {
                ExprNode right = parseExpr4();
                left = new BinaryOpNode(BinaryOpNode.OpType.EQ, opToken, left, right);
            } else if (match(TokenType.SY_NEQ)) {
                ExprNode right = parseExpr4();
                left = new BinaryOpNode(BinaryOpNode.OpType.NEQ, opToken, left, right);
            } else if (match(TokenType.SY_GTE)) {
                ExprNode right = parseExpr4();
                left = new BinaryOpNode(BinaryOpNode.OpType.GTE, opToken, left, right);
            } else if (match(TokenType.SY_LTE)) {
                ExprNode right = parseExpr4();
                left = new BinaryOpNode(BinaryOpNode.OpType.LTE, opToken, left, right);
            } else if (match(TokenType.SY_GT)) {
                ExprNode right = parseExpr4();
                left = new BinaryOpNode(BinaryOpNode.OpType.GT, opToken, left, right);
            } else if (match(TokenType.SY_LT)) {
                ExprNode right = parseExpr4();
                left = new BinaryOpNode(BinaryOpNode.OpType.LT, opToken, left, right);
            } else {
                break;
            }
        }
        return left;
    }

    // Expr4 = Expr4 '+' Expr5 | Expr4 '-' Expr5 | Expr5
    private ExprNode parseExpr4() {
        ExprNode left = parseExpr5();
        while (true) {
            Token opToken = peek();
            if (match(TokenType.SY_ADD)) {
                ExprNode right = parseExpr5();
                left = new BinaryOpNode(BinaryOpNode.OpType.ADD, opToken, left, right);
            } else if (match(TokenType.SY_SUB)) {
                ExprNode right = parseExpr5();
                left = new BinaryOpNode(BinaryOpNode.OpType.SUB, opToken, left, right);
            } else {
                break;
            }
        }
        return left;
    }

    // Expr5 = Expr5 '*' Expr6 | Expr5 '/' Expr6 | Expr6
    private ExprNode parseExpr5() {
        ExprNode left = parseExpr6();
        while (true) {
            Token opToken = peek();
            if (match(TokenType.SY_MUL)) {
                ExprNode right = parseExpr6();
                left = new BinaryOpNode(BinaryOpNode.OpType.MUL, opToken, left, right);
            } else if (match(TokenType.SY_DIV)) {
                ExprNode right = parseExpr6();
                left = new BinaryOpNode(BinaryOpNode.OpType.DIV, opToken, left, right);
            } else {
                break;
            }
        }
        return left;
    }

    // Expr6 = '!' Expr7 | '-' Expr7 | Expr7
    private ExprNode parseExpr6() {
        Token opToken = peek();
        if (match(TokenType.SY_NOT)) {
            ExprNode expr = parseExpr7();
            return new UnaryOpNode(UnaryOpNode.OpType.NOT, opToken, expr);
        } else if (match(TokenType.SY_SUB)) {
            ExprNode expr = parseExpr7();
            return new UnaryOpNode(UnaryOpNode.OpType.NEGATE, opToken, expr);
        } else {
            return parseExpr7();
        }
    }

    // Expr7 = FnCall | '(' Expr ')' | IDENT | LT_INT | LT_FLOAT | LT_STRING |
    // LT_CHAR | LT_BOOL
    private ExprNode parseExpr7() {
        Token peekedToken = peek();
        if (check(TokenType.IDENT)) {
            if (pos + 1 < tokens.size() && tokens.get(pos + 1).getType() == TokenType.SY_LPAREN) {
                return parseFnCall();
            } else {
                return new IdentExprNode(advance());
            }
        } else if (match(TokenType.SY_LPAREN)) {
            ExprNode expr = parseExpr();
            expect(TokenType.SY_RPAREN);
            return expr;
        } else if (match(TokenType.LT_INT)) {
            TokenLiteral token = (TokenLiteral) peekedToken;
            return new IntLiteralNode((Long) token.getValue(), token);
        } else if (match(TokenType.LT_FLOAT)) {
            TokenLiteral token = (TokenLiteral) peekedToken;
            return new FloatLiteralNode((Double) token.getValue(), token);
        } else if (match(TokenType.LT_STRING)) {
            TokenLiteral token = (TokenLiteral) peekedToken;
            return new StringLiteralNode((String) token.getValue(), token);
        } else if (match(TokenType.LT_CHAR)) {
            TokenLiteral token = (TokenLiteral) peekedToken;
            return new CharLiteralNode((Character) token.getValue(), token);
        } else if (match(TokenType.LT_BOOL)) {
            TokenLiteral token = (TokenLiteral) peekedToken;
            return new BoolLiteralNode((Boolean) token.getValue(), token);
        }
        Token token = peek();
        throw new ErrorList(String.format(
            "Expected value, got `%s`", token.getLexeme()
        ), token.getPos());
    }

    // FnCall = IDENT '(' ArgsList ')'
    private FnCallNode parseFnCall() {
        Token name = expect(TokenType.IDENT);
        expect(TokenType.SY_LPAREN);
        List<ExprNode> args = parseArgsList();
        expect(TokenType.SY_RPAREN);
        return new FnCallNode(name, args);
    }

    // ArgsList = Expr ',' ArgsList | Expr | ε
    private List<ExprNode> parseArgsList() {
        List<ExprNode> args = new ArrayList<>();
        if (!check(TokenType.SY_RPAREN)) {
            args.add(parseExpr());
            while (match(TokenType.SY_COMMA)) {
                args.add(parseExpr());
            }
        }
        return args;
    }
}