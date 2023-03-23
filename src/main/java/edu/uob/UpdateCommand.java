package edu.uob;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

public class UpdateCommand extends DBCommand{
    String updateAtrib, updateVal;
    public UpdateCommand(){
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
        this.nameValueList = new ArrayList<>();
        this.updateAtrib = updateVal = "";
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
        if(!checkFileExists()) return;
        getTable();
        if(!isValidTable()) return;
        if(!checkAttributes()) return;
        getAllTableIDs();
        condition();
        updateTable();
        saveTable();
    }

    public boolean notInDatabase() {
        try{
            server.checkInDatabase();
        } catch(IOException e){
            DBServer.output = ("[ERROR]: Must be Using a database to update values in a table");
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

    public void createValueList(String value) {
        value = value.toLowerCase();
        this.values.add(value);
    }

    public void createNameValueList(ArrayList<String> nameValueList) {
        this.nameValueList.add(nameValueList);
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

    public void condition(){
        String conString;
        for (ArrayList<String> condition : conditions) {
            if (condition.size() == 3) {
                conString = getSelectedIDs(condition);
            }else conString = condition.toString();
            conditionList.add(conString);
        }
        iterateIDList();
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

    public void createOutputTable(){
        if(idStack.isEmpty()) return;
        String selectedIDs = idStack.peek();
        int index = table.getColumnIndex(updateAtrib);
        for(int i=0; i<selectedIDs.length(); i++){
            Row row = table.getRowByID(selectedIDs.charAt(i));
            if(row!=null) row.setValue(index, updateVal);
        }
    }

    public void updateTable(){
        for(ArrayList<String> nameValuePair: nameValueList){
            updateAtrib = nameValuePair.get(0);
            updateVal = nameValuePair.get(2);
            createOutputTable();
        }
    }

    public boolean checkAttributes(){
        for(ArrayList<String> nameValuePair: nameValueList){
            String attribute = nameValuePair.get(0);
            try{
                checkAttributeName(attribute);
            }catch(IOException e){
                DBServer.output = ("[ERROR]: Attribute "+attribute+" not found in current table");
                return false;
            }
        }
        return true;
    }

    public void checkAttributeName(String name) throws IOException{
        if(table.searchColumns(name)) throw new IOException("Attribute not found");
    }

    public void saveTable(){
        table.outputTable(filePath);
    }

}
