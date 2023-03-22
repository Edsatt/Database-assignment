package edu.uob;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class DBCommand {

    DBServer server;
    ArrayList<Token> tokens;
    DatabaseList databases;
    Database database;
    Table table;
    List<Integer> idList;
    List<String> tempList;
    Boolean hasList;
    List<String> attributeNames;
    List<String> values;
    ArrayList<ArrayList<String>> conditions;
    String attributeName;
    String commandType;
    ArrayList<Row> rows;
    Row row;
    String id;
    String filePath;
    String idFilePath;
    final String newLine = System.lineSeparator();

    public void interpretCommand(){}

    public void setServer(DBServer server){}

    public void setId(String id){}

    public void createAttributeList(String attributeName){}

    public void createValueList(String value){}

    public void addCondition(String value){}

    public void createConditionList(ArrayList<String> value){}

    public void checkList() throws IOException {}

    public void setCommandType(String commandType){}
}
