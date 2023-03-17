package edu.uob;

import java.util.ArrayList;
import java.util.Objects;

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

    public void parseCommand(){
        if(tokens.get(programCount).getType()!=TokenType.COMMAND){
            parseSuccess = false;
            return;
        }else commandType();
        incrementProgramCount();
        if(!Objects.equals(tokens.get(programCount).getValue(), ";")){
            parseSuccess = false;
        }
    }

    public void commandType(){
        switch (tokens.get(programCount).getValue().toUpperCase()){
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
        Token token = getNextToken();
        if(token.getType()!=TokenType.NAME){
            parseSuccess = false;
        }
    }

    public void parseCreateQuery(){
        Token token = getNextToken();
        String tokenValue = token.getValue().toUpperCase();
        if(Objects.equals(tokenValue, "DATABASE")) {
            parseCreateDatabase();
            return;
        }
        if(Objects.equals(tokenValue, "TABLE")){
            parseCreateTable();
            return;
        }
        parseSuccess = false;
    }

    public void parseCreateDatabase(){
        Token token = getNextToken();
        if(token.getType()!=TokenType.NAME){
            parseSuccess = false;
        }
    }

    public void parseCreateTable(){
        Token token = getNextToken();
        if(token.getType()!=TokenType.NAME){
            parseSuccess = false;
            return;
        }
        token = getNextToken();
        if(Objects.equals(token.getValue(), "(")) parseAttributeList();
        else {
            decrementProgramCount();
        }
    }

    private void parseAttributeList() {
    }

    public void parseDropQuery(){}

    public void parseAlterQuery(){}

    public void parseInsertQuery(){}

    public void parseSelectQuery(){}

    public void parseUpdateQuery(){}

    public void parseDeleteQuery(){}

    public void parseJoinQuery(){}
}
