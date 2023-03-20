package edu.uob;

import java.util.ArrayList;
import java.util.Objects;

import static edu.uob.TokenType.*;

public class Parser {

    private final ArrayList<Token> tokens;
    private int programCount;
    private boolean parseSuccess;
    private boolean withinBraces;
    private int openBracketCnt;
    private int closedBracketCnt;

    public String outputString;
    DBCommand command;

    public Parser(ArrayList<Token> tokens){
        this.tokens = tokens;
        this.programCount = 0;
        this.parseSuccess = true;
        this.withinBraces = false;
        this.outputString = "";
        //System.out.println("Parse success = " +parseSuccess);
    }

    public void outputParseResult(){
        if(isParseSuccess()) outputString = "[OK]";
        DBServer.output = outputString;
    }

    public void logError(String message){
        if(outputString.isEmpty()) outputString = outputString.concat(message);
        else outputString = outputString.concat(System.lineSeparator() +message);
        setParseSuccess(false);
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

    // append error message string to parse fail attribute. After parsing, check string. If null, throw exception with error message.

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

    public void initialiseBrackets(){
        openBracketCnt = closedBracketCnt = 0;
    }

    public void addOpenBracketCnt(){
        openBracketCnt++;
    }

    public void addClosedBracketCnt(){
        closedBracketCnt++;
    }

    public boolean equalNumBraces(){
        return openBracketCnt != closedBracketCnt;
    }

    public DBCommand parseCommand(){
        if(!tokenType(getCurrentToken(), COMMAND)){
            logError("Expecting command?");
        }else commandType();
        incrementProgramCount(1);
        if(!tokenValue(getCurrentToken(), ";")) logError("Expecting ; at end of query?");
        return command;
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
        buildUseCommand();
        incrementProgramCount(1);
        if(!parsePlainText()){
            logError(getCurrentToken().getValue() +" is not a valid database name");
        }
        command.setId(getCurrentToken().getValue());
    }
    public void buildUseCommand(){
        command = new UseCommand();
    }

    public void parseCreateQuery(){
        if(tokenValue(getNextToken(), "DATABASE")){
            buildCreateDBCommand();
            parseCreateDatabase();
            return;
        }
        if(tokenValue(getCurrentToken(), "TABLE")){
            buildCreateTableCommand();
            parseCreateTable();
            return;
        }
        logError("Expecting DATABASE or TABLE following CREATE command");
    }

    public void parseCreateDatabase(){
        incrementProgramCount(1);
        if(!parsePlainText()){
            logError(getCurrentToken().getValue() +" is not a valid database name");
        }command.setId(getCurrentToken().getValue());
    }

    public void buildCreateDBCommand(){
        command = new CreateDBCommand();
    }

    public void parseCreateTable(){
        incrementProgramCount(1);
        if(!parsePlainText()){
            logError(getCurrentToken().getValue() +" is not a valid table name");
            return;
        }command.setId(getCurrentToken().getValue());
        if(checkNextToken("(")) {
            incrementProgramCount(1);
            parseAttributeList();
            if(!tokenValue(getNextToken(), ")")) logError("Expecting braces?");
        }
    }

    public void buildCreateTableCommand(){
        command = new CreateTableCommand();
    }

    private void parseAttributeList() {
        incrementProgramCount(1);
        if(parseAttributeName()){
            command.createList(getCurrentToken().getValue());
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
        String tokenValue = getNextToken().getValue().toUpperCase();
        switch (tokenValue) {
            case "DATABASE", "TABLE" -> {
                interpretDropQuery(tokenValue);
                if(!tokenType(getNextToken(), PLAIN_TEXT)) {
                    logError("invalid DATABASE or TABLE name " +getCurrentToken().getValue());
                }else command.setId(getCurrentToken().getValue());
                return;
            }
        }
        logError("Expecting DATABASE or TABLE following DROP command");
    }

    public void interpretDropQuery(String tokenValue){
        switch(tokenValue){
            case "DATABASE" -> buildDropDBCommand();
            case "TABLE" -> buildDropTableCommand();
        }
    }

    public void buildDropDBCommand(){
        command = new DropDBCommand();
    }

    private void buildDropTableCommand() {
        command = new DropTableCommand();
    }

//    public void buildCreateTableCommand(){
//        command = new CreateTableCommand();
//    }

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
        logError("Error with ALTER command syntax");
    }

    public void parseInsertQuery(){
        if(tokenValue(getNextToken(), "INTO")) {
            incrementProgramCount(1);
            if (parsePlainText()){
                if (tokenValue(getNextToken(), "VALUES")) {
                    if(checkNextToken("(")){
                        incrementProgramCount(1);
                        parseValueList();
                        if(!tokenValue(getNextToken(), ")")) {
                            logError("Missing braces in query");
                        }
                        return;
                    }
                }
            }
        }
        logError("Error with INSERT query syntax");
    }

    public void parseValueList(){
        incrementProgramCount(1);
        if(parseValue()){
            if(checkNextToken(",")){
                incrementProgramCount(1);
                parseValueList();
            }
        } else logError("Expecting value or list of values");
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
                    initialiseBrackets();
                    conditionBraceCheck();
                    if(isWithinBraces() || equalNumBraces()) {
                        logError("Unexpected braces found in query");
                    }
                }
            }
            return;
        }
        logError("Error with SELECT query syntax");
    }
    public void parseWildAttribList(){
        if(!tokenType(getCurrentToken(), WILD)) parseAttributeList();
    }

    public void conditionBraceCheck(){
        if(checkNextToken("(")){
            if(isWithinBraces()) {
                logError("Unexpected brace within query");
                return;
            }
            setWithinBraces(true);
            addOpenBracketCnt();
            incrementProgramCount(1);
            while(checkNextToken("(")) {
                incrementProgramCount(1);
                addOpenBracketCnt();
            }
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
        }else logError("Error with condition syntax");
    }

    public void checkClosingBrace(){
        if(tokenValue(getCurrentToken(), ")")) {
            incrementProgramCount(1);
            addClosedBracketCnt();
            checkClosingBrace();
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
                    initialiseBrackets();
                    conditionBraceCheck();
                    if(equalNumBraces()) {
                        logError("Unexpected braces in query");
                    }
                    return;
                }
            }
        }
        logError("Error with UPDATE query syntax");
    }

    private void parseNameValueList() {
        incrementProgramCount(1);
        if(parseNameValuePair()){
            if(checkNextToken(",")){
                incrementProgramCount(1);
                parseNameValueList();
            }
        } else logError("Expecting name value pair");
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
                    initialiseBrackets();
                    conditionBraceCheck();
                    if(equalNumBraces()) logError("Unexpected braces within query");
                    return;
                }
            }
        }
        logError("Error in DELETE query syntax");
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
        logError("Error in JOIN query syntax");
    }
}
