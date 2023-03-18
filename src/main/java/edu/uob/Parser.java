package edu.uob;

import java.util.ArrayList;
import java.util.Objects;

import static edu.uob.TokenType.*;

public class Parser {

    private ArrayList<Token> tokens;
    private int programCount;
    private boolean parseSuccess;

    public Parser(ArrayList<Token> tokens){
        this.tokens = tokens;
        this.programCount = 0;
        this.parseSuccess = true;
        parseCommand();
        System.out.println("Parse success = " +parseSuccess);
    }

    public boolean isParseSuccess() {
        return parseSuccess;
    }

    public Token getCurrentToken(){
        return tokens.get(programCount);
    }

    public boolean checkNextToken(Object query) {
        if (programCount < tokens.size() - 1) {
            Token nextToken = tokens.get(programCount + 1);
            if(query instanceof String){
                return (tokenValue(nextToken, ((String) query)));
            }
            if(query instanceof TokenType){
                return (tokenType(nextToken, ((TokenType) query)));
            }
        }
        return false;
    }

    public Token getNextToken(){
        incrementProgramCount();
        return tokens.get(programCount);
    }

    public Token getPreviousToken(){
        decrementProgramCount();
        return tokens.get(programCount);
    }

    public void incrementProgramCount(){
        if(programCount<tokens.size()-1) programCount++;
    }

    public void decrementProgramCount(){
        if(programCount>0) programCount --;
    }

    public boolean tokenValue(Token token, String query){
        return Objects.equals(token.getValue().toUpperCase(), query);
    }

    public  boolean tokenType(Token token, TokenType type){
        return Objects.equals(token.getType(), type);
    }

    public void parseCommand(){
        if(!tokenType(getCurrentToken(), COMMAND)){
            parseSuccess = false;
            return;
        }else commandType();
        incrementProgramCount();
        if(!tokenValue(getCurrentToken(), ";")) parseSuccess = false;

    }

    public void commandType(){
        switch (getCurrentToken().getValue().toUpperCase()){
            case "USE" -> parseUseQuery();
            case "CREATE" -> parseCreateQuery();
            case "DROP" -> parseDropQuery();
            case "ALTER" -> parseAlterQuery();
            case "INSERT" -> parseInsertQuery();
            case "SELECT" -> parseSelectQuery();
            case "UPDATE" -> parseUpdateQuery();
            case "DELETE" -> parseDeleteQuery();
            case "JOIN" -> parseJoinQuery();
        }
    }

    public void parseUseQuery(){
        if(!parsePlainText(getNextToken())){
            parseSuccess = false;
        }
    }

    public void parseCreateQuery(){
        if(tokenValue(getNextToken(), "DATABASE")){
            parseCreateDatabase();
            return;
        }
        if(tokenValue(getCurrentToken(), "TABLE")){
            parseCreateTable();
            return;
        }
        parseSuccess = false;
    }

    public void parseCreateDatabase(){
        if(!parsePlainText(getNextToken())){
            parseSuccess = false;
        }
    }

    public void parseCreateTable(){
        if(!parsePlainText(getNextToken())){
            parseSuccess = false;
            return;
        }
        if(tokenValue(getNextToken(), "(")) {
            parseAttributeList();
            if(!tokenValue(getNextToken(), ")")) parseSuccess = false;
        }
        else {
            decrementProgramCount();
        }
    }

    private void parseAttributeList() {
        if(parseAttributeName(getNextToken())){
            if(tokenValue(getNextToken(), ",")){
                parseAttributeList();
            }
        }decrementProgramCount();
    }

    private boolean parseAttributeName(Token token){
        if(parsePlainText(token)){
            if(checkNextToken(".")){
                getNextToken();
                if(parsePlainText(getNextToken())) return true;
            }
            return true;
        }
        return false;
    }

    private boolean parsePlainText(Token token){
        switch (token.getType()) {
            case INTEGER, LETTER, PLAIN_TEXT -> {
                return true;
            }
        }
        return false;
    }

    public void parseDropQuery(){
        switch (getNextToken().getValue().toUpperCase()) {
            case "DATABASE", "TABLE" -> {
                if(!tokenType(getNextToken(), PLAIN_TEXT)) parseSuccess = false;
                return;
            }
        }
        parseSuccess = false;
    }

    public void parseAlterQuery(){
        if(tokenValue(getNextToken(), "TABLE")){
            if(tokenValue(getNextToken(), "NAME")){
                switch (getNextToken().getValue().toUpperCase()){
                    case "ADD", "DROP" -> {
                        parseAttributeName(getNextToken());
                        return;
                    }
                }
            }
        }
        parseSuccess = false;
    }
//<Insert>          ::=  "INSERT INTO " [TableName] " VALUES(" <ValueList> ")"

    public void parseInsertQuery(){
        if(tokenValue(getNextToken(), "INTO")) {
            if (parsePlainText(getNextToken())) {
                if (tokenValue(getNextToken(), "VALUES")) {
                    if(tokenValue(getNextToken(), "(")){
                        parseValueList();
                        return;
                    }
                }
            }
        }
        parseSuccess = false;
    }

    public void parseValueList(){
        getNextToken();
        if(tokenValue(getCurrentToken(), ")")) return;
        if(parseValue()){
            if(checkNextToken(",")) incrementProgramCount();
            parseValueList();
        } else parseSuccess = false;
    }

    public boolean parseValue(){
        if(parseStringLiteral()) return true;
        if(parseBooleanLiteral()) return true;
        if(parseFloatOrInteger()) return true;
        return parseNull();
    }

    public boolean parseNull(){
        return tokenType(getCurrentToken(), NULL);
    }

    public boolean parseStringLiteral(){
        return tokenType(getCurrentToken(), STRING_LITERAL);
    }

    public boolean parseBooleanLiteral(){
        return (tokenType(getCurrentToken(), BOOLEAN));
    }

    public boolean parseFloatOrInteger(){
        switch (getCurrentToken().getValue()) {
            case "-", "+" -> {
                getNextToken();
            }
        }
        return parseFloat() || parseInteger();
    }

    public boolean parseFloat(){
        if(parseDigitSequence()){
            if(checkNextToken(".")){
                getNextToken();
                getNextToken();
                return parseDigitSequence();
            }
        }
        return false;
    }

    public boolean parseInteger(){
        return parseDigitSequence();
    }

    public boolean parseDigitSequence(){
        if(!tokenType(getCurrentToken(), INTEGER)) return false;
        while(checkNextToken(INTEGER)){
            getNextToken();
        }
        return true;
    }

    //<Select>::=  "SELECT " <WildAttribList> " FROM " [TableName] | "SELECT " <WildAttribList> " FROM " [TableName] " WHERE " <Condition>
    //<WildAttribList>  ::=  <AttributeList> | "*"
    //<Condition>       ::=  "(" <Condition> [BoolOperator] <Condition> ")" | <Condition> [BoolOperator] <Condition>
    // | "(" [AttributeName] [Comparator] [Value] ")" | [AttributeName] [Comparator] [Value]
    public void parseSelectQuery() {
        incrementProgramCount();
        parseWildAttribList();
        if (tokenValue(getNextToken(), "FROM")) {
            if (tokenType(getNextToken(), PLAIN_TEXT)) {
                if (checkNextToken("WHERE")) {
                    incrementProgramCount();
                    parseCondition();
                }else return;
            }
        }
        parseSuccess = false;
    }
    public void parseWildAttribList(){
        if(!tokenType(getCurrentToken(), WILD)) parseAttributeList();
    }


    public void parseCondition(){
        if(parseConditionComparator()){
            incrementProgramCount();
            if(parseBoolOp()){
                incrementProgramCount();
                parseCondition();
            }
        }else parseSuccess = false;
    }

    public boolean parseBoolOp(){
        switch(getCurrentToken().getValue().toUpperCase()){
            case "AND", "OR" -> {
                return true;
            }
        }
        return false;
    }

    public boolean parseConditionComparator(){
        parseAttributeName(getCurrentToken());
        if(tokenType(getNextToken(),COMPARATOR)) {
            return parseValue();
        }
        return false;
    }

    public void parseUpdateQuery(){}

    public void parseDeleteQuery(){}

    public void parseJoinQuery(){}
}
