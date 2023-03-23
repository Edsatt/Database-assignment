package edu.uob;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Stack;

public class SelectCommand extends DBCommand{

    public SelectCommand(){
        this.databases = DBServer.databases;
        this.tempList = new ArrayList<>();
        this.attributeNames = new ArrayList<>();
        this.conditions = new ArrayList<>();
        this.values = new ArrayList<>();
        this.hasList = false;
        this.attribute = this.comparator = this.value = "";
        this.conditionList = new ArrayList<>();
        this.tableIDs = new ArrayList<>();
        this.idStack = new Stack<>();
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
        if(notInDatabase()) return;
        this.filePath = server.getCurrentFolderPath().concat(File.separator +id.toLowerCase()+".tab");
        if(!checkFileExists()) return;
        getTable();
        if(!isValidTable()) return;
        if(!isValidList()) return;
        if(!checkAttributes()) return;
        getAllTableIDs();
        outputTable();
    }


    public boolean notInDatabase() {
        try{
            server.checkInDatabase();
        } catch(IOException e){
            DBServer.output = ("[ERROR]: Must be Using a database to select values from a table");
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

    public boolean isValidTable(){
        try{
            tableCheck();
        }catch(IOException e){
            DBServer.output = ("[ERROR]: Table "+id+" has no attributes");
            return false;
        }
        return true;
    }

    public boolean isValidList(){
        if(!allSelected()){
            try {
                checkList();
            } catch (IOException e) {
                DBServer.output = ("[ERROR]: Table referenced by attribute list does not match selected table");
                return false;
            }
        }
        return true;
    }

    public void outputTable(){
        if(!hasCondition()){
            if(allSelected()) {
                DBServer.output = DBServer.output.concat
                        (newLine+table.printTable());
            }else{
                DBServer.output = DBServer.output.concat
                        (newLine+createOutputTable(table).printTable());
            }
        }else{
            if(allSelected()) {
                DBServer.output = DBServer.output.concat
                        (newLine+createInputTable().printTable());
            }
            else {
                DBServer.output = DBServer.output.concat
                        (newLine+createOutputTable(createInputTable()).printTable());
            }
        }
    }

    public boolean allSelected(){
        if(commandType==null) return false;
        return (commandType.equals("WILD"));
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
        Table output = new Table(attributeNames);
        int i=0;
        for(Row row: input.getRows().values()){
            if(!row.getRowName().equalsIgnoreCase("columnNames")){
                Row newRow = input.modifyRow(row,indexes);
                output.addRow("row"+i,newRow);
                i++;
            }
        }
        return output;
    }

    public boolean hasCondition(){
        if(conditions.size()==0) return false;
        String conString;
        for (ArrayList<String> condition : conditions) {
            if (condition.size() == 3) {
                conString = getSelectedIDs(condition);
            }else conString = condition.toString();
            conditionList.add(conString);
        }
        iterateIDList();
        return true;
    }

    public String getSelectedIDs(ArrayList<String> condition){
        this.attribute = condition.get(0).toLowerCase();
        this.comparator = condition.get(1).toLowerCase();
        this.value = condition.get(2).toLowerCase();
        try{
            checkAttributeName(attribute);
        }catch(IOException e){
            DBServer.output = ("[ERROR]: Attribute "+attribute+" not found in current table");
        }
        return (table.modifyTable(table,attribute,value,comparator));
    }

    public void getAllTableIDs(){
        for(Row row: table.getRows().values()){
            if(!row.getRowName().equalsIgnoreCase("columnNames")){
                char id = row.getId(row);
                tableIDs.add(id);
            }
        }
    }

    public void iterateIDList(){
        for (String s : conditionList) {
            if (s.matches(".*\\b(and|or|\\d+)\\b.*")) {
                idStack.push(s);
            }
            if(idStack.size()==3) combineIDList();
        }
    }

    public void combineIDList(){
        String IDList1 = idStack.pop();
        String boolFunc = idStack.pop();
        String IDList2 = idStack.pop();
        ArrayList<Character> output = new ArrayList<>();

        for(char id: tableIDs){
            switch(boolFunc){

                case "[or]" -> {
                    if(IDList1.contains(Character.toString(id)) || IDList2.contains(Character.toString(id))){
                        output.add(id);
                    }
                }
                case "[and]" -> {
                    if(IDList1.contains(Character.toString(id)) && IDList2.contains(Character.toString(id))){
                        output.add(id);
                    }
                }
            }

        }
        idStack.push(output.toString());
    }

    public Table createInputTable(){
        ArrayList<String> columnNames = table.getColumns().getValues();
        Table inputTable = new Table(columnNames);
        if(idStack.isEmpty()) return inputTable;
        String selectedIDs = idStack.pop();
        for(int i=0; i<selectedIDs.length(); i++){
            Row row = table.getRowByID(selectedIDs.charAt(i));
            if(row!=null) inputTable.addRow("row"+i,row);
        }
        return inputTable;
    }

    public boolean checkAttributes(){
        for(String name: attributeNames){
            try{
                checkAttributeName(name);
            }catch(IOException e){
                DBServer.output = ("[ERROR]: Attribute "+name+" not found in current table");
                return false;
            }
        }
        return true;
    }

    public void checkAttributeName(String name) throws IOException{
        if(table.searchColumns(name)) throw new IOException("Attribute not found");
    }

}
