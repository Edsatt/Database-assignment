package edu.uob;

import javax.xml.crypto.Data;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Objects;

public class Interpreter {

    DatabaseList databases;
    Database database;
    ArrayList<Token> tokens;
    int programCounter;
    public Interpreter(ArrayList<Token> tokens){
        this.databases = DBServer.databases;
        this.tokens = tokens;
    }

    public void interpretUseQuery(Token token){
        String tokenValue = token.getValue();
        for(String databaseName: databases.getDatabases().keySet())
            if (Objects.equals(tokenValue.toLowerCase(), databaseName.toLowerCase())) {
                this.database = databases.getDatabase(databaseName);
                return;
            }
        System.out.println("Database with name " +tokenValue +" not found");
    }

    public void createDatabase(Token token){
        String databaseName = token.getValue();
        Database database = new Database();
        databases.addDatabase(databaseName, database);
    }

    public void createTable(Token token){
        String tableName = token.getValue();
        Table table = new Table(tableName, null);
        database.addTable(tableName, table);
    }

    public void createTableFromList(Token token, int programCount){

    }


}
