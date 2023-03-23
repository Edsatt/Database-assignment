package edu.uob;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class JoinCommand extends DBCommand{

    String id1, id2, attribute1, attribute2;
    String filePath1, filePath2;
    Table table1;
    Table table2;
    ArrayList<String> joinedCols;
    Table joinedTable;
    int index1;
    int index2;
    int rowNum;
    public JoinCommand(){
        this.id1 = this.id2 = this.attribute1 = this.attribute2 = "";
        this.filePath1 = filePath2 = "";
        this.joinedCols = new ArrayList<>();
        this.rowNum = 0;
    }

    public void setServer(DBServer server) {
        this.server = server;
    }


    public void addJoinList(ArrayList<String> joinList){
        id1 = joinList.get(0);
        id2 = joinList.get(1);
        attribute1 = joinList.get(2);
        attribute2 = joinList.get(3);
    }

    public void interpretCommand() {
        if(notInDatabase()) return;
        this.filePath1 = server.getCurrentFolderPath().concat(File.separator + id1.toLowerCase()+".tab");
        this.filePath2 = server.getCurrentFolderPath().concat(File.separator + id2.toLowerCase()+".tab");
        if(!checkExists()) return;
        getTables();
        if(!checkTables()) return;
        if(!checkAttributes()) return;
        initJoinedTable();
        setIndexes();
        joinTables();
        addIds();
        DBServer.output = (joinedTable.printTable());
    }

    public boolean notInDatabase() {
        try{
            server.checkInDatabase();
        } catch(IOException e){
            DBServer.output = ("[ERROR]: Must be Using a database to join tables");
            return true;
        }
        return false;
    }

    public boolean checkExists(){
        try{
            server.fileExists(filePath1,true);
        } catch(IOException e){
            DBServer.output = ("[ERROR]: "+table1+" not found in current database");
            return false;
        }
        try{
            server.fileExists(filePath2,true);
        } catch(IOException e){
            DBServer.output = ("[ERROR]: "+table2+" not found in current database");
            return false;
        }
        return true;
    }

    public void getTables(){
        table1 = getTable(id1, filePath1);
        table2 = getTable(id2, filePath2);
    }

    public Table getTable(String id, String fp){
        if(server.getDatabase().searchDatabase(id)){
            return server.getDatabase().getTable(id);
        }else return server.importTable(fp);
    }

    public boolean checkTables(){
        try{
            tableCheck(table1);
        }catch(IOException e){
            DBServer.output = ("[ERROR]: "+id1+" has no attributes");
            return false;
        }
        try{
            tableCheck(table2);
        }catch(IOException e){
            DBServer.output = ("[ERROR]: "+id2+" has no attributes");
            return false;
        }
        return true;
    }

    public void tableCheck(Table table) throws IOException {
        if (table == null) throw new IOException("Empty table");
    }

    public boolean checkAttributes(){
        try{
            checkAttributeName(table1, attribute1);
        }catch (IOException e){
            DBServer.output = ("[ERROR]: attribute "+attribute1+" not found in table "+id1);
            return false;
        }
        try{
            checkAttributeName(table2, attribute2);
        }catch (IOException e){
            DBServer.output = ("[ERROR]: attribute "+attribute2+" not found in table "+id2);
            return false;
        }
        return true;
    }

    public void checkAttributeName(Table table, String name) throws IOException{
        if(table.searchColumns(name)) throw new IOException("Attribute not found");
    }

    public void initJoinedTable(){
        ArrayList<String> table1ColNames = table1.getColumns().getValues();
        ArrayList<String> table2ColNames = table2.getColumns().getValues();
        makeJoinedColumns(table1ColNames, "table1");
        makeJoinedColumns(table2ColNames, "table2");
        joinedTable = new Table(joinedCols);
    }

    public void makeJoinedColumns(ArrayList<String>columnNames, String tableNum){
        for(String columnName: columnNames){
            switch(tableNum){
                case "table1" ->{
                    if(!columnName.equalsIgnoreCase("id") && !columnName.equalsIgnoreCase(attribute1)){
                        joinedCols.add(id1+"."+columnName);
                    }
                }
                case "table2" ->{
                    if(!columnName.equalsIgnoreCase("id") && !columnName.equalsIgnoreCase(attribute2)){
                        joinedCols.add(id2+"."+columnName);
                    }
                }
            }
        }
    }

    public void setIndexes(){
        index1 = table1.getColumnIndex(attribute1);
        index2 = table2.getColumnIndex(attribute2);
    }

    public void joinTables(){
        String value;
        for(Row t1Row: table1.getRows().values()){
            value = t1Row.getValueByColumn(index1);
            matchRows(value, t1Row);
        }
    }

    public void matchRows(String query, Row t1Row){
        String value;
        for(Row t2Row: table2.getRows().values()){
            value = t2Row.getValueByColumn(index2);
            if(query.equalsIgnoreCase(value)){
                joinRows(t1Row, t2Row);
            }
        }
    }

    public void joinRows(Row t1Row, Row t2Row) {
        List<String> rowString = new ArrayList<>();
        rowString.addAll(cleanRow(t1Row, "table1"));
        rowString.addAll(cleanRow(t2Row, "table2"));
        String rowName = "row"+rowNum;
        rowNum++;
        Row joinedRow = new Row(rowName, rowString);
        joinedTable.addRow(rowName,joinedRow);
    }

    public List<String> cleanRow (Row row, String tableNum){
        List<String> rowString = row.getValues();
        switch(tableNum){
            case "table1"->{
                if(index1>0) rowString.remove(index1);
            }
            case "table2"->{
                if(index2>0) rowString.remove(index2);
            }
        }
        List<String> cleanString = new ArrayList<>(rowString);
        removeId(cleanString);
        return cleanString;
    }

    public void removeId(List<String> rowString){
        rowString.remove(0);
    }

    public void addIds(){
        int idCount = 0;
        for(Row row: joinedTable.getRows().values()){
            if(row.getRowName().equalsIgnoreCase("columnNames")){
                row.addValue(0,"id");
            }else{
                row.addValue(0, ""+idCount);
                idCount++;
            }
        }
    }
}

