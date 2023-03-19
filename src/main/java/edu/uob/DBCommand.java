package edu.uob;

import java.util.ArrayList;

public abstract class DBCommand {
    ArrayList<Token> tokens;
    DatabaseList databases;
    Database database;
    Table table;
    ArrayList<Row> rows;
    Row row;
    String id;

    public void interpretCommand(){}

    public void setId(String id){}
}
