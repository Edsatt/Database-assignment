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
        this.tempList = new ArrayList<>();
        this.attributeNames = new ArrayList<>();
        hasList = false;
    }

    public void setServer(DBServer server) {
        this.server = server;
    }

    public void setId(String dbName){
        this.id = dbName;
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
            try{
                checkList();
            }catch(IOException e){
                DBServer.output = ("[ERROR]"+newLine+"Table referenced by attribute list does not match selected table");
                return;
            }
            createTable();
            saveTable();
        }
    }

    public void createAttributeList(String attributeName){
        hasList = true;
        this.tempList.add(attributeName);
    }

    public void checkList() throws IOException{
        String [] splitSting;
        String tableName;
        String attribute;
        for(String attributeName: tempList){
            if(attributeName.contains(".")){
                splitSting = attributeName.split("\\.");
                tableName = splitSting[0];
                attribute = splitSting[1];
                if(!tableNameCheck(tableName)){
                    throw new IOException("Table name doesn't match current table");
                }
                attributeNames.add(attribute);
            }else attributeNames.add(attributeName);
        }
    }

    public boolean tableNameCheck(String tableName){
        return Objects.equals(tableName, id);
    }

    public void createTable(){
        table = new Table(id, attributeNames);
        if(Objects.equals(table.getColumnName(1), "id")){
            try {
                table.removeColumn("id");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        server.getDatabase().addTable(id, table);
    }

    public void saveTable(){
        table.outputTable(filePath);
    }
}