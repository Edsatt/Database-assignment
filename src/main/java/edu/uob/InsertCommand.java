package edu.uob;

import java.io.*;
import java.util.ArrayList;

public class InsertCommand extends DBCommand{

    int tableSize;
    public InsertCommand(){
        this.databases = DBServer.databases;
        this.values = new ArrayList<>();
        this.idList = new ArrayList<>();
    }

    public void setServer(DBServer server) {
        this.server = server;
    }

    public void setId(String dbName){
        this.id = dbName;
    }

    public void interpretCommand() {
        if(notInDatabase()) return;
        this.filePath = server.getCurrentFolderPath().concat(File.separator +id.toLowerCase()+".tab");
        this.idFilePath = server.getCurrentFolderPath().concat(File.separator+id.toLowerCase()+"_id.txt");
        if(!checkFileExists()) return;
        getTable();
        if(!checkTableIsValid()) return;
        if(!checkNumberOfValues()) return;
        getIdFile();
        addRow();
        saveTable();
        saveIdFile();
    }

    public void createValueList(String value) {
        this.values.add(value);
    }

    public boolean notInDatabase(){
        try{
            server.checkInDatabase();
        } catch(IOException e){
            DBServer.output = ("[ERROR]: Must be Using a database to insert values into a table");
            return true;
        }
        return false;
    }

    public boolean checkFileExists() {
        try{
            server.fileExists(filePath,true);
        } catch(IOException e){
            DBServer.output = ("[ERROR]: Table "+id+" not found in current database");
            return false;
        }
        return true;
    }

    public boolean checkTableIsValid(){
        try{
            tableCheck();
        }catch(IOException e){
            DBServer.output = ("[ERROR]: Table "+id+" has no attributes");
            return false;
        }
        return true;
    }

    public boolean checkNumberOfValues(){
        try{
            rowCheck();
        }catch(IOException e){
            DBServer.output = ("[ERROR]: Expecting " +tableSize+" values");
            return false;
        }
        return true;
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
        values.add(0, ""+createNewId());
        row = new Row(rowName,values);
        table.addRow(rowName,row);
    }

    public void saveTable(){
        table.outputTable(filePath);
    }

    public void getIdFile(){
        File fileToOpen = new File(idFilePath);
        try{
            FileReader reader = new FileReader(fileToOpen);
            BufferedReader buffReader = new BufferedReader(reader);
            String id;
            while((id = buffReader.readLine()) != null){
                importIds(id);
            }
            buffReader.close();
        } catch(IOException ioe){
            System.out.println("Cannot open file");
        }
    }

    public void importIds(String id){
        idList.add(Integer.parseInt(id));
    }


    public int createNewId(){
        int id = idList.size()+1;
        idList.add(id);
        return id;
    }

    public void saveIdFile(){
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(idFilePath));
            for (int id: idList) {
                writer.write(Integer.toString(id));
                writer.println();
            }
            writer.close();
        } catch(IOException ioe) {
            System.out.println("Can't write to id file");
        }
    }
}
