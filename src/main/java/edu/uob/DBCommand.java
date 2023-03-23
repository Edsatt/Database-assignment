package edu.uob;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public abstract class DBCommand {

    DBServer server;
    DatabaseList databases;
    Database database;
    Table table;
    List<Integer> idList;
    List<String> tempList;
    Boolean hasList;
    List<String> attributeNames;
    List<String> values;
    ArrayList<ArrayList<String>> conditions;
    ArrayList<ArrayList<String>> nameValueList;
    String attributeName;
    String commandType;
    Row row;
    String id;
    String filePath;
    String idFilePath;
    String attribute, comparator, value;
    ArrayList<String> conditionList;
    ArrayList<Character> tableIDs;
    Stack<String> idStack;
    final String newLine = System.lineSeparator();

    public abstract void interpretCommand();

    public void setServer(DBServer server){}

    public void setId(String id){}

    public boolean notInDatabase(){return true;}

    public boolean checkFileExists(){return false;}

    public void createAttributeList(String attributeName){}

    public void createValueList(String value){}

    public void addCondition(String value){}

    public void createConditionList(ArrayList<String> value){}

    public void checkList() throws IOException {}

    public void setCommandType(String commandType){}

    public void createNameValueList(ArrayList<String> nameValueList) {}

    public void addJoinList(ArrayList<String> joinList){}
}
