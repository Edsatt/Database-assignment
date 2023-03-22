package edu.uob;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class SelectCommand extends DBCommand{

    String attribute, comparator, value;
    Table tempTable;

    public SelectCommand(){
        this.databases = DBServer.databases;
        this.tempList = new ArrayList<>();
        this.attributeNames = new ArrayList<>();
        this.conditions = new ArrayList<>();
        this.values = new ArrayList<>();
        this.hasList = false;
        this.attribute = this.comparator = this.value = "";
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
            DBServer.output = ("[ERROR]"+newLine+"Must be Using a database to select values from a table");
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
        if(!isWildList()){
            try {
                checkList();
            } catch (IOException e) {
                DBServer.output = ("[ERROR]" + newLine + "Table referenced by attribute list does not match selected table");
                return;
            }
        }
        if(!checkAttributes()) return;
        condition();
        //createOutputTable(tempTable).printTable("output");
        //System.out.println(tempTable.getTableName());
        //tempTable.printTable("output");
    }

    public boolean isWildList(){
        if(commandType==null) return false;
        return(commandType.equals("WILD"));
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
                attributeNames.add(attribute);
            }else attributeNames.add(attributeName);
        }
    }

    public boolean tableNameCheck(String tableName){
        return Objects.equals(tableName, id);
    }

    public void createValueList(String value) {
        value = value.toLowerCase();
        this.values.add(value);
    }

    public void addCondition(String value){
        ArrayList<String> condition = new ArrayList<>();
        condition.add(value.toLowerCase());
        conditions.add(condition);
    }

    public void createConditionList(ArrayList<String> value){
        this.conditions.add(value);
    }

    public void getTable(){
        if(server.getDatabase().searchDatabase(id)){
            table = server.getDatabase().getTable(id);
        }else table = server.importTable(filePath);
    }

    public void tableCheck() throws IOException {
        if (table == null) throw new IOException("Empty table");
    }

    public Table createOutputTable(Table input){
        ArrayList<Integer> indexes = new ArrayList<>();
        for(String attribute: attributeNames){
            int index = input.getColumnIndex(attribute);
            indexes.add(index);
        }
        Table output = new Table("output", attributeNames);
        int i=0;
        for(Row row: input.getRows().values()){
            if(!row.getRowName().equalsIgnoreCase("columnnames")){
                Row newRow = input.modifyRow(row,indexes);
                output.addRow("row"+i,newRow);
                i++;
            }
        }
        return output;
    }

    public void condition(){
        for (int i=0; i<conditions.size(); i++) {
            Table tempTable1 = null;
            if (conditions.get(i).size() == 3) {
                tempTable1 = makeTempTable(conditions.get(i));
                tempTable1.printTable("output");
            }
            i++;
            String boolFunc = conditions.get(i).get(0);
            i++;
            Table tempTable2 = null;
            if (conditions.get(i).size() == 3) {
                tempTable2 = makeTempTable(conditions.get(i));
                tempTable2.printTable("output");
            }
            Condition condition = new Condition(tempTable1, tempTable2, boolFunc);
        }
    }

    public Table makeTempTable(ArrayList<String> condition){
        this.attribute = condition.get(0).toLowerCase();
        this.comparator = condition.get(1).toLowerCase();
        this.value = condition.get(2).toLowerCase();
        try{
            checkAttributeName(attribute);
        }catch(IOException e){
            DBServer.output = ("[ERROR]"+newLine+"Attribute "+attribute+" not found in current table");
        }
        return table.modifyTable(table,attribute,value,comparator);
    }

    public boolean checkAttributes(){
        for(String name: attributeNames){
            try{
                checkAttributeName(name);
            }catch(IOException e){
                DBServer.output = ("[ERROR]"+newLine+"Attribute "+name+" not found in current table");
                return false;
            }
        }
        return true;
    }

    public void checkAttributeName(String name) throws IOException{
        if(!table.searchColumns(name)) throw new IOException("Attribute not found");
    }

}
