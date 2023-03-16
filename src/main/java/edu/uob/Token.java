package edu.uob;

public class Token {
    private String value;
    private TokenType type;

    public Token(String token){
        this.value = token;
        this.type = setTypeSpecific(token);
    }

    public TokenType setTypeSpecific (String token){
        switch (token) {
            case "USE", "CREATE", "DATABASE", "TABLE", "Add", "DROP", "ALTER", "INSERT", "INTO", "VALUES",
                    "SELECT", "FROM", "WHERE", "UPDATE", "SET", "DELETE", "JOIN", "AND", "ON", "OR" -> {
                return TokenType.COMMAND;
            }
            case "TRUE", "FALSE" -> {
                return TokenType.BOOLEAN;
            }
            case "==", ">",  "<",  ">=",  "<=",  "!=",  "LIKE" -> {
                return TokenType.COMPARATOR;
            }
            case "!", "#", "$", "%", "&", "(", ")", "+", ",", "-", ".", "/", ":", ";", "=", "?",
                    "@", "[", "\"", "]", "^", "_", "`", "{", "}", "~" -> {
                return TokenType.SYMBOL;
            }
            case "*" -> {
                return TokenType.WILD;
            }
            case " " -> {
                return TokenType.SPACE;
            }
        }
        return setTypeNonSpecific(token);
    }

    public TokenType setTypeNonSpecific(String token){
        if(token.startsWith("'") && token.endsWith("'")) return TokenType.STRING_LITERAL;
        char[] tokenArray = token.toCharArray();
        for(char c: tokenArray){
            if(Character.isDigit(c)){
                if(token.contains(".")) return TokenType.FLOAT;
                else return TokenType.INTEGER;
            }
            if(Character.isAlphabetic(c)){
                if(token.length()>1) return TokenType.NAME;
                else return TokenType.LETTER;
            }
        }
        return TokenType.INVALID;
    }

    public String getValue() {
        return value;
    }

    public TokenType getType() {
        return type;
    }
}
