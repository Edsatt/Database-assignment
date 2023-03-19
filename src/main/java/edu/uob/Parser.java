package edu.uob;

import java.util.ArrayList;
import java.util.Objects;

import static edu.uob.TokenType.*;

public class Parser {

    private final ArrayList<Token> tokens;
    private int programCount;
    private boolean parseSuccess;
    private boolean withinBraces;

    public Parser(ArrayList<Token> tokens){
        this.tokens = tokens;
        this.programCount = 0;
        this.parseSuccess = true;
        this.withinBraces = false;
        parseCommand();
        System.out.println("Parse success = " +parseSuccess);
    }

    public boolean isParseSuccess() {
        return parseSuccess;
    }

    public void setParseSuccess(boolean parseSuccess) {
        this.parseSuccess = parseSuccess;
    }

    public boolean isWithinBraces() {
        return withinBraces;
    }

    public void setWithinBraces(boolean withinBraces){
        this.withinBraces = withinBraces;
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
        incrementProgramCount(1);
        return tokens.get(programCount);
    }

    public void incrementProgramCount(int increment){
        if(programCount<tokens.size()-increment) programCount = programCount+increment;
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
            setParseSuccess(false);
            return;
        }else commandType();
        incrementProgramCount(1);
        if(!tokenValue(getCurrentToken(), ";")) setParseSuccess(false);
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
        incrementProgramCount(1);
        if(!parsePlainText()){
            setParseSuccess(false);
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
        setParseSuccess(false);
    }

    public void parseCreateDatabase(){
        incrementProgramCount(1);
        if(!parsePlainText()){
            setParseSuccess(false);
        }
    }

    public void parseCreateTable(){
        incrementProgramCount(1);
        if(!parsePlainText()){
            setParseSuccess(false);
            return;
        }
        if(checkNextToken("(")) {
            incrementProgramCount(1);
            parseAttributeList();
            if(!tokenValue(getNextToken(), ")")) setParseSuccess(false);
        }
    }

    private void parseAttributeList() {
        incrementProgramCount(1);
        if(parseAttributeName()){
            if(checkNextToken(",")){
                incrementProgramCount(1);
                parseAttributeList();
            }
        }
    }

    private boolean parseAttributeName(){
        if(parsePlainText()){
            if(checkNextToken(".")){
                incrementProgramCount(2);
                if(parsePlainText()) return true;
            }
            return true;
        }
        return false;
    }

    private boolean parsePlainText(){
        switch (getCurrentToken().getType()) {
            case INTEGER, LETTER, PLAIN_TEXT -> {
                return true;
            }
        }
        return false;
    }

    public void parseDropQuery(){
        switch (getNextToken().getValue().toUpperCase()) {
            case "DATABASE", "TABLE" -> {
                if(!tokenType(getNextToken(), PLAIN_TEXT)) setParseSuccess(false);
                return;
            }
        }
        setParseSuccess(false);
    }

    public void parseAlterQuery(){
        if(tokenValue(getNextToken(), "TABLE")){
            if(tokenValue(getNextToken(), "NAME")){
                switch (getNextToken().getValue().toUpperCase()){
                    case "ADD", "DROP" -> {
                        incrementProgramCount(1);
                        parseAttributeName();
                        return;
                    }
                }
            }
        }
        setParseSuccess(false);
    }

    public void parseInsertQuery(){
        if(tokenValue(getNextToken(), "INTO")) {
            incrementProgramCount(1);
            if (parsePlainText()){
                if (tokenValue(getNextToken(), "VALUES")) {
                    if(checkNextToken("(")){
                        incrementProgramCount(1);
                        parseValueList();
                        if(!tokenValue(getNextToken(), ")")) setParseSuccess(false);
                        return;
                    }
                }
            }
        }
        setParseSuccess(false);
    }

    public void parseValueList(){
        incrementProgramCount(1);
        if(parseValue()){
            if(checkNextToken(",")){
                incrementProgramCount(1);
                parseValueList();
            }
        } else setParseSuccess(false);
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
            case "-", "+" -> getNextToken();
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

    public void parseSelectQuery() {
        parseWildAttribList();
        if (tokenValue(getNextToken(), "FROM")) {
            if (tokenType(getNextToken(), PLAIN_TEXT)) {
                if (checkNextToken("WHERE")) {
                    incrementProgramCount(1);
                    conditionBraceCheck();
                    if(isWithinBraces()) setParseSuccess(false);
                }
            }
            return;
        }
        setParseSuccess(false);
    }
    public void parseWildAttribList(){
        if(!tokenType(getCurrentToken(), WILD)) parseAttributeList();
    }

    public void conditionBraceCheck(){
        if(checkNextToken("(")){
            if(isWithinBraces()) {
                setParseSuccess(false);
                return;
            }
            setWithinBraces(true);
            incrementProgramCount(1);
            parseCondition();
        }else parseCondition();
    }

    public void parseCondition(){
        incrementProgramCount(1);
        if(parseConditionComparator()){
            incrementProgramCount(1);
            if(isWithinBraces()) checkClosingBrace();
            if(parseBoolOp()){
                conditionBraceCheck();
            }else decrementProgramCount();
        }else setParseSuccess(false);
    }

    public void checkClosingBrace(){
        if(tokenValue(getCurrentToken(), ")")) {
            incrementProgramCount(1);
            setWithinBraces(false);
        }
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
        parseAttributeName();
        if(tokenType(getNextToken(),COMPARATOR)) {
            incrementProgramCount(1);
            return parseValue();
        }
        return false;
    }

    public void parseUpdateQuery(){
        if(tokenType(getNextToken(), PLAIN_TEXT)){
            if(tokenValue(getNextToken(),"SET")){
                parseNameValueList();
                if(tokenValue(getNextToken(), "WHERE")){
                    conditionBraceCheck();
                    return;
                }
            }
        }
        setParseSuccess(false);
    }

    private void parseNameValueList() {
        incrementProgramCount(1);
        if(parseNameValuePair()){
            if(checkNextToken(",")){
                incrementProgramCount(1);
                parseNameValueList();
            }
        } else setParseSuccess(false);
    }

    private boolean parseNameValuePair(){
        if(parseAttributeName()){
            if(tokenValue(getNextToken(), "=")){
                incrementProgramCount(1);
                return(parseValue());
            }
        }
        return false;
    }
    public void parseDeleteQuery(){
        if(tokenValue(getNextToken(), "FROM")){
            incrementProgramCount(1);
            if(parsePlainText()){
                if(tokenValue(getNextToken(),"WHERE")){
                    conditionBraceCheck();
                    return;
                }
            }
        }
        setParseSuccess(false);
    }

    public void parseJoinQuery(){
        incrementProgramCount(1);
        if(parsePlainText()){
            if(tokenValue(getNextToken(), "AND")){
                incrementProgramCount(1);
                if(parsePlainText()){
                    if(tokenValue(getNextToken(), "ON")){
                        incrementProgramCount(1);
                        parseAttributeName();
                        if(tokenValue(getNextToken(), "AND")) {
                            incrementProgramCount(1);
                            parseAttributeName();
                            return;
                        }
                    }
                }
            }
        }
        setParseSuccess(false);
    }
}
