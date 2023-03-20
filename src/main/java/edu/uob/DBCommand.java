package edu.uob;

import java.util.ArrayList;
import java.util.List;

public abstract class DBCommand {

    DBServer server;
    ArrayList<Token> tokens;
    DatabaseList databases;
    Database database;
    Table table;

    List<String> columnNames;
    ArrayList<Row> rows;
    Row row;
    String id;

    String filePath;
    final String newLine = System.lineSeparator();

    public void interpretCommand(){}

    public void setServer(DBServer server){}

    public void setId(String id){}

    public void createList(String columnName){}
}
