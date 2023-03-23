package edu.uob;
import java.util.*;

public class Database {
    private final LinkedHashMap<String, Table> tables;
    private final String databaseName;

    public Database(String name){
        tables = new LinkedHashMap<>();
        this.databaseName = name;
    }

    public String getDatabaseName(){
        return databaseName;
    }

    public Table getTable(String tableName){
        return tables.get(tableName);
    }

    public void addTable(String tableName, Table table){
        tables.put(tableName, table);
    }

    public void removeTable(String tableName){
        tables.remove(tableName);
    }

    public boolean searchDatabase(String name){
        for(String tableName: tables.keySet()){
            if(Objects.equals(tableName, name)){
                return true;
            }
        } return false;
    }
}
