package edu.uob;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;

public class CreateTableCommand extends DBCommand{

    public CreateTableCommand(){
        this.databases = DBServer.databases;
        this.tempList = new ArrayList<>();
        this.attributeNames = new ArrayList<>();
        this.hasList = false;
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
        this.idFilePath = server.getCurrentFolderPath().concat(File.separator+id.toLowerCase()+"_id.txt");
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
        }
        createIDFIle();
        if(hasList){
            try{
                checkList();
            }catch(IOException e){
                DBServer.output = ("[ERROR]"+newLine+"Table referenced by attribute list does not match selected table");
                return;
            }
            try{
                idCheck();
            }catch(IOException e){
                DBServer.output = ("[ERROR]"+newLine+"id is not a valid column name");
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

    public void idCheck ()throws IOException{
        for(String columnName: attributeNames){
            if(columnName.equalsIgnoreCase("id")) {
                throw new IOException("Cannot name column ID");
            }
        }
        attributeNames.add(0, "id");
    }

    public boolean tableNameCheck(String tableName){
        return Objects.equals(tableName, id);
    }

    public void createIDFIle(){
        try {
            Files.createFile(Paths.get(idFilePath));
        } catch (IOException e) {
            DBServer.output = ("[ERROR]"+newLine+"Cannot create id file");
        }
    }

    public void createTable(){
        table = new Table(attributeNames);
        server.getDatabase().addTable(id, table);
    }

    public void saveTable(){
        table.outputTable(filePath);
    }
}