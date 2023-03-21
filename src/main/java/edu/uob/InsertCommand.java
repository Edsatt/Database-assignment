package edu.uob;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class InsertCommand extends DBCommand{

    int tableSize;
    public InsertCommand(){
        this.databases = DBServer.databases;
        this.values = new ArrayList<>();
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
            DBServer.output = ("[ERROR]"+newLine+"Must be Using a database to insert values into a table");
            return;
        }
        this.filePath = server.getCurrentFolderPath().concat(File.separator +id.toLowerCase()+".tab");
        try{
            server.fileExists(filePath,true);
        } catch(IOException e){
            DBServer.output = ("[ERROR]"+newLine+"Table "+id+" not found in current database");
            return;
        }
        getTable();

        try{
            tableCheck();
        }catch(IOException e){
            DBServer.output = ("[ERROR]"+newLine+"Table "+id+" has no attributes");
            return;
        }
        try{
            rowCheck();
        }catch(IOException e){
            DBServer.output = ("[ERROR]"+newLine+"Expecting " +tableSize+" values");
            return;
        }
        addRow();
        saveTable();
    }

    public void createValueList(String value) {
        if(value.startsWith("'") && value.endsWith("'")){
            value = value.replaceAll("'", "");
        }
        value = value.toLowerCase();
        this.values.add(value);
    }

    public void getTable(){
        if(server.getDatabase().searchDatabase(id)){
            table = server.getDatabase().getTable(id);
        }else table = server.importTable(filePath);
    }

    public void tableCheck() throws IOException{
        if(table == null) throw new IOException("Empty table");
        tableSize = table.getNumCols()-1;
    }

    public void rowCheck() throws IOException{
        if(values.size()!= tableSize) throw new IOException("Invalid number of values");
    }

    public void addRow(){
        String rowName = "row "+table.getNumRows();
        values.add(0,"NULL");
        row = new Row(rowName,values);
        table.addRow(rowName,row);
        table.addId(table);
    }

    public void saveTable(){
        table.outputTable(filePath);
    }
}
