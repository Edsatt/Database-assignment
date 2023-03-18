package edu.uob;

public class Token {
    private String value;
    private TokenType type;

    public Token(String token){
        this.value = token;
        this.type = setTypeSpecific(token);
    }

    public TokenType setTypeSpecific (String token){
        switch (token.toUpperCase()) {
            case "USE", "CREATE", "DATABASE", "TABLE", "ADD", "DROP", "ALTER", "INSERT", "INTO", "VALUES",
                    "SELECT", "FROM", "WHERE", "UPDATE", "SET", "DELETE", "JOIN", "AND", "ON", "OR" -> {
                return TokenType.COMMAND;
            }
            case "TRUE", "FALSE" -> {
                return TokenType.BOOLEAN;
            }
            case "NULL" -> {
                return TokenType.NULL;
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
        if(token.startsWith("'") && token.endsWith("'")) return checkStringLiteral(token);
        char[] tokenArray = token.toCharArray();
        for(char c: tokenArray){
            if(Character.isAlphabetic(c)){
                if(token.length()>1) return TokenType.PLAIN_TEXT;
                else return TokenType.LETTER;
            }
            if(!Character.isDigit(c)) return TokenType.INVALID;
        }
        return TokenType.INTEGER;
    }

    public TokenType checkStringLiteral(String token){
        char[] tokenArray = token.toCharArray();
        for(int i=1; i<tokenArray.length-1; i++){
            if(!Character.isLetterOrDigit(tokenArray[i])){
                return TokenType.INVALID;
            }
        }
        return TokenType.STRING_LITERAL;
    }

    public String getValue() {
        return value;
    }

    public TokenType getType() {
        return type;
    }
}
