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
        try{
            server.checkInDatabase();
        } catch(IOException e){
            DBServer.output = ("[ERROR]"+newLine+"Must be Using a database to insert values into a table");
            return;
        }
        this.filePath = server.getCurrentFolderPath().concat(File.separator +id.toLowerCase()+".tab");
        this.idFilePath = server.getCurrentFolderPath().concat(File.separator+id.toLowerCase()+"_id.txt");
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
        getIdFile();
        addRow();
        saveTable();
        saveIdFile();
    }

    public void createValueList(String value) {
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
        int id = idList.size();
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
