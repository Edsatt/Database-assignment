package edu.uob;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

//Table class contains a hashmap for its rows and keys
public class Table {
    private LinkedHashMap<String, Row> rows;
    private LinkedHashMap<String, Key> keys;

    private String tableName;
    //add method to check table has id column, and if not add it in

    public Table(String tableName, List<String> values){
        this.rows = new LinkedHashMap<>();
        this.keys = new LinkedHashMap<>();
        this.tableName = tableName;
        Row columnNames = new Row("columnNames", values);
        columnNames.addValue(0,"id");
        addRow("columnNames", columnNames);
    }

    public void addId(Table table){
        int i=0;
        for(Row row: table.getRows().values()){
            if(!Objects.equals(row.getRowName(), "columnNames")){
                row.addValue(0,""+i);
            }
            i++;
        }
    }

    public HashMap<String, Row> getRows() {
        return rows;
    }

    public Row getRow(String rowName){
        return rows.get(rowName);
    }

    public String getColumnName(int columnIndex){
        return getRow("columnNames").getValueByColumn(columnIndex);
    }

    public int getColumnIndex(String columnName){
        return getRow("columnNames").getColumnIndex(columnName);
    }

    public Key getKey(String colName){
        return keys.get(colName);
    }

    public String getTableName() {
        return tableName;
    }

    public void addRow(String rowName, Row row){
        rows.put(rowName, row);
    }

    //inserts column and shifts index of other columns by 1
    public void addColumn(int columnIndex, String columnName){
        getRow("columnNames").addValue(columnIndex, columnName);
    }

    //replace a column
    public void setColumn(int columnIndex, String columnName){
        getRow("columnNames").setValue(columnIndex, columnName);
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    //add multiple columns
    public void addColumnList(List<String> columnNames){
        getRow("columnNames").addValueList(columnNames);
    }

    public void addKey(String colName, Key key){
        keys.put(colName, key);
    }

    public void removeRow(String rowName){
        rows.remove(rowName);
    }

    public void removeColumn(String columnName){
        int index = getRow("columnNames").getColumnIndex(columnName); //gets index for specified column name
        for (Row row: rows.values()){
            row.removeValue(row.getValueByColumn(index));
        }
    }

    public void removeKey(String colName){
        keys.remove(colName);
    }

    public int getNumRows(){
        return rows.size();
    }

    public int getNumCols(){
        return getRow("columnNames").getNumValues();
    }

    //need to write an exception for this method in case table doesn't exist
    public void outputTable(String filePath){
        try {
            BufferedWriter buffWriter = new BufferedWriter(new FileWriter(filePath));
            for (Row row : rows.values()) {
                String rowString = String.join("\t", row.getValues());
                buffWriter.write(rowString);
                buffWriter.newLine();
            }
            buffWriter.close();
        } catch(IOException ioe) {
            System.out.println("Can't write to file");
        }
    }

    public void printTable(String tableName){
        for (Row row : rows.values()) {
            String rowString = String.join("\t", row.getValues());
            System.out.println(rowString);
        }
    }

    public void printTableKeys(){
        for(String name: rows.keySet()){
            System.out.println(name);
        }
    }
}
