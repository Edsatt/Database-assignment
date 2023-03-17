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

    public Token getCurrentToken(){
        return tokens.get(programCount);
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

    public void parseCommand(){
        if(getCurrentToken().getType()!=TokenType.COMMAND){
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
        if(tokenValue(getNextToken(), "(")) parseAttributeList(getNextToken());
        else {
            decrementProgramCount();
        }
    }

    private void parseAttributeList(Token token) {
        if(tokenValue(token,")")){
            return;
        }
        if(tokenValue(token, ",")) token = getNextToken();
        parseAttributeName(token);
    }

    private void parseAttributeName(Token token){
        Token nextToken = tokens.get(programCount+1);
        if(parsePlainText(token)){
            if(tokenValue(nextToken, ".")){
                getNextToken();
                if(parsePlainText(getNextToken())){
                    parseAttributeList(getNextToken());
                }else parseSuccess = false;
            }else parseAttributeList(getNextToken());
        } else parseSuccess = false;
    }

    private boolean parsePlainText(Token token){
        switch (token.getType()) {
            case INTEGER, LETTER, PLAIN_TEXT -> {
                return true;
            }
        }
        return false;
    }

    public void parseDropQuery(){}

    public void parseAlterQuery(){}

    public void parseInsertQuery(){}

    public void parseSelectQuery(){}

    public void parseUpdateQuery(){}

    public void parseDeleteQuery(){}

    public void parseJoinQuery(){}
}
