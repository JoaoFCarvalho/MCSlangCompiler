package compiler.lexer;

import org.junit.jupiter.api.Test;

import compiler.error.ErrorList;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class LexerTest {
    @Test void basicTest1() {
        String input = "var x: int = 42;";
        List<Token> tokens = Lexer.tokenize(input);
        assertEquals(8, tokens.size());
        assertEquals(TokenType.KW_VAR, tokens.get(0).getType());
        assertEquals(TokenType.IDENT, tokens.get(1).getType());
        assertEquals("x", tokens.get(1).getLexeme());
        assertEquals(TokenType.SY_COLON, tokens.get(2).getType());
        assertEquals(TokenType.TY_INT, tokens.get(3).getType());
        assertEquals(TokenType.SY_ASSIGN, tokens.get(4).getType());
        assertEquals(TokenType.LT_INT, tokens.get(5).getType());
        assertEquals("42", tokens.get(5).getLexeme());
        assertEquals(TokenType.SY_SEMICOLON, tokens.get(6).getType());
        assertEquals(TokenType.EOF, tokens.get(7).getType());
    }

    @Test void basicTest2() {
        // Example with an invalid token
        String input = "var x: int = 42 @;";
        assertThrows(ErrorList.class, () -> Lexer.tokenize(input));
    }
}
