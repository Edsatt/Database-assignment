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

    public void incrementProgramCount(){
        if(programCount<tokens.size()-1) programCount++;
    }

    public void parseCommand(){
        if(tokens.get(programCount).getType()!=TokenType.COMMAND){
            parseSuccess = false;
            return;
        }else incrementProgramCount();
        if(!Objects.equals(tokens.get(programCount).getValue(), ";")){
            parseSuccess = false;
        }
    }

    public void commandType(){
        switch (tokens.get(programCount).getValue()){
            case "USE" -> parseUse();
            case "CREATE" -> parseCreate();
            case "DROP" -> parseDrop();
            case "ALTER" -> parseAlter();
            case "INSERT" -> parseInsert();
            case "SELECT" -> parseSelect();
            case "UPDATE" -> parseUpdate();
            case "DELETE" -> parseDelete();
            case "JOIN" -> parseJoin();
        }
    }

    public void parseUse(){}

    public void parseCreate(){}

    public void parseDrop(){}

    public void parseAlter(){}

    public void parseInsert(){}

    public void parseSelect(){}

    public void parseUpdate(){}

    public void parseDelete(){}

    public void parseJoin(){}

}
