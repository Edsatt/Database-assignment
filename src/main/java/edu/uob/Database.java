package edu.uob;
import java.util.*;
//TableList contains a hashmap of the different table names
public class Database {
    private LinkedHashMap<String, Table> tables;

    public Database(){
        tables = new LinkedHashMap<>();
    }

    public LinkedHashMap<String, Table> getTables(){
        return tables;
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

    public int getNumTables(){
        return tables.size();
    }


    public void printTableNames(){
        for(String name: tables.keySet()){
            System.out.println(name);
        }
    }

    public void outputTables(String storageFolderPath){
        for(String tableName: tables.keySet()){
            getTable(tableName).outputTable(storageFolderPath, tableName);
        }
    }

    public void printTables(){
        for(String tableName: tables.keySet()){
            getTable(tableName).printTable(tableName);
        }
    }
}
