package edu.uob;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TokeniserTests {
    Tokeniser tokeniser;

    @Test
    public void tokeniseTests(){
        tokeniser = new Tokeniser("SELECT name FROM marks WHERE mark>60;");
        assertEquals(tokeniser.getNumTokens(), 9);
        assertEquals(tokeniser.getToken(0).getValue(), "SELECT");
        assertEquals(tokeniser.getToken(0).getType(), TokenType.COMMAND);
        assertEquals(tokeniser.getToken(7).getValue(), "60");
        assertEquals(tokeniser.getToken(7).getType(), TokenType.INTEGER);
    }

    @Test
    public void splitTests(){
        tokeniser = new Tokeniser("60>6.0<=a+A+Aa'Aa'");
        assertEquals(tokeniser.getNumTokens(), 12);
        assertEquals(tokeniser.getToken(2).getValue(), "6");
        assertEquals(tokeniser.getToken(2).getType(), TokenType.INTEGER);
        assertEquals(tokeniser.getToken(5).getValue(), "<=");
        assertEquals(tokeniser.getToken(5).getType(), TokenType.COMPARATOR);
        assertEquals(tokeniser.getToken(6).getValue(), "a");
        assertEquals(tokeniser.getToken(6).getType(), TokenType.LETTER);
        assertEquals(tokeniser.getToken(10).getValue(), "Aa");
        assertEquals(tokeniser.getToken(10).getType(), TokenType.PLAIN_TEXT);
    }
}
