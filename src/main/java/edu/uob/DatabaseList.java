package edu.uob;

import java.util.HashMap;

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

    public Database getDatabase(String name){
        return databases.get(name);
    }

    public void removeDatabase(String name){
        databases.remove(name);
    }
}
