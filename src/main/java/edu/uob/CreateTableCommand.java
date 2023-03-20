package edu.uob;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;

public class CreateTableCommand extends DBCommand{

    private boolean hasList;

    public CreateTableCommand(){
        this.databases = DBServer.databases;
        this.columnNames = new ArrayList<>();
        hasList = false;
    }

    public void setServer(DBServer server) {
        this.server = server;
    }

    public void setId(String dbName){
        this.id = dbName;
    }

    public void setHasList(boolean hasList) {
        this.hasList = hasList;
    }

    public boolean HasList() {
        return hasList;
    }

    public void interpretCommand() {
        try{
            server.checkInDatabase();
        } catch(IOException e){
            DBServer.output = ("[ERROR]"+newLine+"Must be Using a database to create a table");
            return;
        }
        this.filePath = server.getCurrentFolderPath().concat(File.separator +id.toLowerCase()+".tab");
        try{
            server.fileExists(filePath,false);
        } catch(IOException e){
            DBServer.output = ("[ERROR]"+newLine+"Table "+id+" already exists");
            return;
        }
        try{
            Files.createFile(Paths.get(filePath));
        }catch(IOException e){
            DBServer.output = ("[ERROR]"+newLine+"Cannot create Table");
            return;
        }if(hasList){
            createTable();
            saveTable();
        }

    }

    public void createList(String columnName){
        System.out.println(columnName);
        hasList = true;
        this.columnNames.add(columnName);
    }

    public void createTable(){
        table = new Table(id, columnNames);
        if(Objects.equals(table.getColumnName(1), "id")){
            table.removeColumn("id");
        }
        server.getDatabase().addTable(id, table);
    }

    public void saveTable(){
        table.outputTable(filePath);
    }
}