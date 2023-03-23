package edu.uob;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class AlterCommand extends DBCommand{

    public AlterCommand(){
        this.databases = DBServer.databases;
        this.tempList = new ArrayList<>();
        this.attributeNames = new ArrayList<>();
    }

    public void setServer(DBServer server) {
        this.server = server;
    }

    public void setId(String dbName){
        this.id = dbName;
    }

    public void setCommandType(String commandType){
        this.commandType = commandType;
    }

    public void interpretCommand() {
        try{
            server.checkInDatabase();
        } catch(IOException e){
            DBServer.output = ("[ERROR]"+newLine+"Must be Using a database to alter a table");
            return;
        }
        this.filePath = server.getCurrentFolderPath().concat(File.separator +id.toLowerCase()+".tab");
        try{
            server.fileExists(filePath,true);
        } catch(IOException e){
            DBServer.output = ("[ERROR]"+newLine+"Table "+id+" not found in current database");
            return;
        }
        try{
            checkList();
        }catch(IOException e){
            DBServer.output = ("[ERROR]"+newLine+"Table referenced by attribute list does not match selected table");
            return;
        }
        getTable();
        switch (commandType){
            case "ADD" -> addColumn();
            case "DROP" -> {
                try{
                    setAttributeName();
                }catch(IOException e){
                    DBServer.output = ("[ERROR]"+newLine+"Can't remove id column");
                    return;
                }
                try{
                    dropColumn();
                }catch (IOException e){
                    DBServer.output = ("[ERROR]"+newLine+"Column "+attributeName+" not found in table "+id);
                    return;
                }
            }
        }
        saveTable();
    }

    public void createAttributeList(String attributeName){
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
                this.attributeNames.add(attribute);
            }else this.attributeNames.add(attributeName);
        }
    }

    public void setAttributeName() throws IOException{
        this.attributeName = attributeNames.get(0);
        if(Objects.equals(attributeName,"id")) throw new IOException("Cannot remove id column");
    }

    public boolean tableNameCheck(String tableName){
        return Objects.equals(tableName, id);
    }

    public void getTable(){
        if(server.getDatabase().searchDatabase(id)){
            table = server.getDatabase().getTable(id);
        }else table = server.importTable(filePath);
    }

    public void addColumn(){
        if (table == null){
            table = new Table(attributeNames);
        } else table.addColumnList(attributeNames);
    }

    public void dropColumn() throws IOException{
        if(table == null) throw new IOException("Empty table");
        else if (table.searchColumns(attributeName)) throw new IOException("Column not found");
        table.removeColumn(attributeName);
    }

    public void saveTable(){
        table.outputTable(filePath);
    }
}