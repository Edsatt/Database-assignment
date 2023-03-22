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
    private String attributeName;
    DBCommand command;
    private String digitString;
    private  ArrayList<String> condition;

    public Parser(ArrayList<Token> tokens){
        this.tokens = tokens;
        this.programCount = 0;
        this.parseSuccess = true;
        this.withinBraces = false;
        this.outputString = "";
        this.attributeName = "";
        this.digitString = "";
        this.condition = new ArrayList<>();
    }

    public void outputParseResult(){
        if(isParseSuccess()) outputString = "[OK]";
        DBServer.output = outputString;
    }

    public void logError(String message){
        if(outputString.isEmpty()) outputString = message;
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
            saveAttributeName();
            if(checkNextToken(",")){
                incrementProgramCount(1);
                parseAttributeList();
            }
        }
    }

    private boolean parseAttributeName(){
        if(parsePlainText()){
            storeAttributeName(getCurrentToken().getValue());
            if(checkNextToken(".")){
                incrementProgramCount(2);
                if(parsePlainText()) {
                    storeAttributeName("."+getCurrentToken().getValue());
                    return true;
                }
            }
            return true;
        }
        return false;
    }

    public void storeAttributeName(String string){
        attributeName = attributeName.concat(string);
    }

    public void saveAttributeName(){
        command.createAttributeList(attributeName);
        attributeName = "";
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
        command = new DropCommand();
        switch(tokenValue){
            case "DATABASE" -> command.setCommandType("DATABASE");
            case "TABLE" -> command.setCommandType("TABLE");
        }
    }

    public void parseAlterQuery(){
        if(tokenValue(getNextToken(), "TABLE")){
            incrementProgramCount(1);
            if(parsePlainText()){
                String tableID = getCurrentToken().getValue();
                String tokenValue = getNextToken().getValue().toUpperCase();
                switch (tokenValue){
                    case "ADD", "DROP" -> {
                        interpretAlterQuery(tokenValue);
                        command.setId(tableID);
                        incrementProgramCount(1);
                        parseAttributeName();
                        saveAttributeName();
                        return;
                    }
                }
            }
        }
        logError("Error with ALTER command syntax");
    }

    public void interpretAlterQuery(String tokenValue){
        command = new AlterCommand();
        switch(tokenValue){
            case "ADD" -> command.setCommandType("ADD");
            case "DROP" -> command.setCommandType("DROP");
        }
    }

    public void parseInsertQuery(){
        buildInsertCommand();
        if(tokenValue(getNextToken(), "INTO")) {
            incrementProgramCount(1);
            if (parsePlainText()){
                command.setId(getCurrentToken().getValue());
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

    public void buildInsertCommand(){
        command = new InsertCommand();
    }

    public void parseValueList(){
        incrementProgramCount(1);
        if(parseValue()){
            if(checkNextToken(",")){
                incrementProgramCount(1);
                parseValueList();
            }
        } else logError("Expecting string literal, boolean, integer, float or null");
    }

    public boolean parseValue(){
        if(parseStringLiteral()) {
            command.createValueList(getCurrentToken().getValue());
            return true;
        }
        if(parseBooleanLiteral()) {
            command.createValueList(getCurrentToken().getValue());
            return true;
        }
        if(parseFloatOrInteger()) {
            saveDigitString();
            return true;
        }
        if(parseNull()){
            command.createValueList(getCurrentToken().getValue());
            return true;
        }
        return false;
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
                buildDigitString(getCurrentToken().getValue());
                getNextToken();
            }

        }
        return parseDigitSequence();
    }

    public boolean parseDigitSequence(){
        if(isInteger()){
            incrementDigitSequence();
            if(checkNextToken(".")){
                incrementProgramCount(2);
                buildDigitString(".");
                incrementDigitSequence();
            }
            return true;
        }
        return false;
    }

    public boolean isInteger(){
        return tokenType(getCurrentToken(), INTEGER);
    }

    public void incrementDigitSequence(){
        buildDigitString(getCurrentToken().getValue());
        if(!checkNextToken(INTEGER)) return;
        incrementProgramCount(1);
        incrementDigitSequence();
    }

    public void buildDigitString(String value){
        digitString = digitString.concat(value);
    }

    public void saveDigitString(){
        command.createValueList(digitString);
        digitString = "";
    }

    public void parseSelectQuery() {
        buildSelectCommand();
        parseWildAttribList();
        if (tokenValue(getNextToken(), "FROM")) {
            if (tokenType(getNextToken(), PLAIN_TEXT)) {
                command.setId(getCurrentToken().getValue());
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

    public void buildSelectCommand(){
        command = new SelectCommand();
    }

    public void parseWildAttribList(){
        if(checkNextToken(WILD)) {
            incrementProgramCount(1);
            command.setCommandType("WILD");
        }
        else parseAttributeList();
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
            command.addCondition(getCurrentToken().getValue());
            while(checkNextToken("(")) {
                incrementProgramCount(1);
                command.addCondition(getCurrentToken().getValue());
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
            command.addCondition(getCurrentToken().getValue());
            incrementProgramCount(1);
            addClosedBracketCnt();
            checkClosingBrace();
            setWithinBraces(false);
        }
    }

    public boolean parseBoolOp(){
        switch(getCurrentToken().getValue().toUpperCase()){
            case "AND", "OR" -> {
                command.addCondition(getCurrentToken().getValue());
                return true;
            }
        }
        return false;
    }

    public boolean parseConditionComparator(){
        saveCondition(programCount);
        parseAttributeName();
        if(tokenType(getNextToken(),COMPARATOR)) {
            incrementProgramCount(1);
            return parseValue();
        }
        return false;
    }

    public void saveCondition(int pc){
        for(int i=0; i<3; i++){
            makeConditionList(tokens.get(pc+i).getValue());
        }
        saveConditionList();
    }

    public void makeConditionList(String value){
        condition.add(value);
    }

    public void saveConditionList(){
        command.createConditionList(condition);
        condition = new ArrayList<>();
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
