// {"node_type":"ProgramNode","decls":[{"node_type":"VarDeclNode","name":"global","type":"string","expr":{"node_type":"StringLiteralNode","value":"this is a global variable!\n"}},{"node_type":"FuncDeclNode","name":"test","params":[{"node_type":"ParamNode","name":"a","type":"float"},{"node_type":"ParamNode","name":"b","type":"float"}],"returnType":"bool","body":{"node_type":"CompoundStmtNode","statements":[{"node_type":"WhileNode","condition":{"node_type":"BinaryOpNode","op":"lt","lhs":{"node_type":"IdentExprNode","name":"a"},"rhs":{"node_type":"FloatLiteralNode","value":1.0E-5}},"body":{"node_type":"AssignmentNode","name":"a","op":"sub_assign","expr":{"node_type":"IdentExprNode","name":"b"}}},{"node_type":"ForNode","init":{"node_type":"VarDeclNode","name":"i","type":"int","expr":{"node_type":"IntLiteralNode","value":0}},"cond":{"node_type":"BinaryOpNode","op":"lt","lhs":{"node_type":"IdentExprNode","name":"i"},"rhs":{"node_type":"IntLiteralNode","value":10}},"step":{"node_type":"AssignmentNode","name":"i","op":"add_assign","expr":{"node_type":"IntLiteralNode","value":1}},"body":{"node_type":"CompoundStmtNode","statements":[{"node_type":"FnCallNode","name":"print","args":[{"node_type":"StringLiteralNode","value":"iter: "},{"node_type":"IdentExprNode","name":"i"}]}]}},{"node_type":"FnCallNode","name":"input","args":[{"node_type":"IdentExprNode","name":"b"}]},{"node_type":"ConditionalNode","condition":{"node_type":"BinaryOpNode","op":"gt","lhs":{"node_type":"BinaryOpNode","op":"mul","lhs":{"node_type":"IdentExprNode","name":"a"},"rhs":{"node_type":"IdentExprNode","name":"b"}},"rhs":{"node_type":"FloatLiteralNode","value":100.0}},"thenBranch":{"node_type":"CompoundStmtNode","statements":[{"node_type":"AssignmentNode","name":"a","op":"assign","expr":{"node_type":"UnaryOpNode","op":"negate","operand":{"node_type":"IdentExprNode","name":"a"}}}]},"elseBranch":{"node_type":"CompoundStmtNode","statements":[{"node_type":"AssignmentNode","name":"b","op":"mul_assign","expr":{"node_type":"UnaryOpNode","op":"negate","operand":{"node_type":"IntLiteralNode","value":1}}}]}},{"node_type":"ReturnNode","expr":{"node_type":"BinaryOpNode","op":"and","lhs":{"node_type":"BinaryOpNode","op":"gt","lhs":{"node_type":"IdentExprNode","name":"a"},"rhs":{"node_type":"IdentExprNode","name":"b"}},"rhs":{"node_type":"BinaryOpNode","op":"lte","lhs":{"node_type":"IdentExprNode","name":"a"},"rhs":{"node_type":"BinaryOpNode","op":"mul","lhs":{"node_type":"IntLiteralNode","value":2},"rhs":{"node_type":"IdentExprNode","name":"b"}}}}}]}}]}
package compiler.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import compiler.error.ErrorList;
import compiler.lexer.Lexer;

class ParserTest {
    @Test void basicTest1() {
        // Test code for the parser
        final String testCode = """
            func main() {
                print("Hello, world!");
            }
            """;

        final String expected = """
            {"node_type":"ProgramNode","decls":[{"node_type":"FuncDeclNode","nam
            e":"main","params":[],"returnType":null,"body":{"node_type":"Compoun
            dStmtNode","statements":[{"node_type":"FnCallNode","name":"print","a
            rgs":[{"node_type":"StringLiteralNode","value":"Hello, world!"}]}]}}
            ]}
            """.replace("\n", "");
        
        var tokens = Lexer.tokenize(testCode);
        var ast = Parser.parse(tokens);

        assertEquals(ast.toString(), expected);
    }

    @Test void basicTest2() {
        // Test code for the parser with an invalid code example
        final String testCode = """
            func main() {
                var x: int = 10;
                x++;
            }
            """;

        var tokens = Lexer.tokenize(testCode);
        assertThrows(ErrorList.class, () -> Parser.parse(tokens));
    }
}