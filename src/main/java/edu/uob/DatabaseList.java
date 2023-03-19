package edu.uob;

import java.util.HashMap;
import java.util.Objects;

public class DatabaseList {

    HashMap<String, Database> databases;
    public DatabaseList(){
        this.databases = new HashMap<>();
    }

    public HashMap<String, Database> getDatabases() {
        return databases;
    }

    public void addDatabase(String name, Database database){
        databases.put(name, database);
    }

    public void searchDatabase(String name) throws DatabaseNotFoundException{
        for(String dbName: databases.keySet()){
            if(Objects.equals(dbName, name)){
                return;
            }
        }
        throw new DatabaseNotFoundException("Database with name " +name +" not found");
    }

    public Database getDatabase(String name){
        return databases.get(name);
    }

    public void removeDatabase(String name){
        databases.remove(name);
    }
}
