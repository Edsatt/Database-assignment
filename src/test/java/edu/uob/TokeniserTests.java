package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TokeniserTests {
    Tokeniser tokeniser;

    @BeforeEach
    public void setup() {
        tokeniser = new Tokeniser();
    }

    @Test
    public void tokeniseTests(){
        tokeniser.setup("SELECT name FROM marks WHERE mark>60;");
        assertEquals(tokeniser.getNumTokens(), 9);
        assertEquals(tokeniser.getToken(0).getValue(), "SELECT");
        assertEquals(tokeniser.getToken(0).getType(), TokenType.COMMAND);
        assertEquals(tokeniser.getToken(7).getValue(), "60");
        assertEquals(tokeniser.getToken(7).getType(), TokenType.INTEGER);
    }

    @Test
    public void splitTests(){
        tokeniser.setup("60>6.0<=a+A+Aa'Aa'");
        assertEquals(tokeniser.getNumTokens(), 10);
        assertEquals(tokeniser.getToken(2).getValue(), "6.0");
        assertEquals(tokeniser.getToken(2).getType(), TokenType.FLOAT);
        assertEquals(tokeniser.getToken(3).getValue(), "<=");
        assertEquals(tokeniser.getToken(3).getType(), TokenType.COMPARATOR);
        assertEquals(tokeniser.getToken(4).getValue(), "a");
        assertEquals(tokeniser.getToken(4).getType(), TokenType.LETTER);
        assertEquals(tokeniser.getToken(8).getValue(), "Aa");
        assertEquals(tokeniser.getToken(8).getType(), TokenType.NAME);
    }
}
