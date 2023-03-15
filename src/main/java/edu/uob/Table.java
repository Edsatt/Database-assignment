package edu.uob;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

//Table class contains a hashmap for its rows and keys
public class Table {
    private LinkedHashMap<String, Row> rows;
    private LinkedHashMap<String, Key> keys;

    public Table(){
        this.rows = new LinkedHashMap<>();
        this.keys = new LinkedHashMap<>();
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

    public void addRow(String rowName, Row row){
        rows.put(rowName, row);
    }

    //inserts column and shifts index of other columns by 1
    public void addColumn(int columnIndex, String columnName){
        getRow("columnNames").addValue(columnIndex, columnName);
    }

    //replace a column
    public void setColumns(int columnIndex, String columnName){
        getRow("columnNames").setValue(columnIndex, columnName);
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
        getRow("columnNames").removeValue(columnName);
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

//    public void printTableKeys(){
//        for(String name: rows.keySet()){
//            System.out.println(name);
//        }
//    }
}
