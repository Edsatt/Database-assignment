package edu.uob;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import static edu.uob.ValueType.*;

public class Table {
    private final LinkedHashMap<String, Row> rows;

    public Table(List<String> values){
        this.rows = new LinkedHashMap<>();
        Row columnNames = new Row("columnNames", values);
        addRow("columnNames", columnNames);
    }

    public HashMap<String, Row> getRows() {
        return rows;
    }

    public Row getRow(String rowName){
        return rows.get(rowName);
    }

    public Row getRowByID(char id){
        for(Row row: rows.values()){
            if(row.getId(row) == id){
                return row;
            }
        }
        return null;
    }

    public String getColumnName(int columnIndex){
        return getRow("columnNames").getValueByColumn(columnIndex);
    }

    public int getColumnIndex(String columnName){
        return getRow("columnNames").getColumnIndex(columnName);
    }

    public Row getColumns(){
        return getRow("columnNames");
    }

    public boolean searchColumns(String query){
        Row columnNames = getRow("columnNames");
        return !columnNames.contains(query);
    }

    public void addRow(String rowName, Row row){
        rows.put(rowName, row);
    }

    public void addColumnList(List<String> columnNames){
        getRow("columnNames").addValueList(columnNames);
    }

    public void removeRow(String rowName){
        rows.remove(rowName);
    }

    public Row modifyRow(Row row, ArrayList<Integer> indexes){
        ArrayList<String> rowString = new ArrayList<>();
        for(int index: indexes){
            String value = row.getValueByColumn(index);
            rowString.add(value);
        }
        return new Row("temp", rowString);
    }

    public void removeColumn(String columnName)throws IOException{
        int index = getRow("columnNames").getColumnIndex(columnName);
        if(index<0) throw new IOException("Column not found");
        for (Row row: rows.values()){
            row.removeValue(row.getValueByColumn(index));
        }
    }

    public int getNumRows(){
        return rows.size();
    }

    public int getNumCols(){
        return getRow("columnNames").getNumValues();
    }

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

    public String printTable(){
        String tableString ="";
        for (Row row : rows.values()) {
            String rowString = String.join("\t", row.getValues()).replace("'", "");
            tableString = tableString.concat(rowString +System.lineSeparator());
        }
        return tableString;
    }

    public String modifyTable(Table input, String attributeName, String value, String comparator){
        int columnIndex = input.getColumnIndex(attributeName);
        ArrayList<Character> output = new ArrayList<>();
        for(String rowName: input.rows.keySet()){
            if(!rowName.equalsIgnoreCase("columnNames")){
                Row row = input.getRow(rowName);
                switch(comparator){
                    case "like" -> {
                        if(compareLike(row, columnIndex, value)){
                            output.add(row.getId(row));
                        }
                    }
                    case "==" -> {
                        if(compareEquals(row, columnIndex, value)){
                            output.add(row.getId(row));
                        }
                    }
                    case "!=" -> {
                        if(!compareEquals(row, columnIndex, value)){
                            output.add(row.getId(row));
                        }
                    }
                    case ">" -> {
                        if(getType(value)==DOUBLE){
                            if(compareGreaterThan(row, columnIndex, value)){
                                output.add(row.getId(row));
                            }
                        }
                    }
                    case "<" -> {
                        if(getType(value)==DOUBLE){
                            if(compareLessThan(row, columnIndex, value)){
                                output.add(row.getId(row));
                            }
                        }
                    }
                    case ">=" -> {
                        if(getType(value)==DOUBLE){
                            if(!compareLessThan(row, columnIndex, value)){
                                output.add(row.getId(row));
                            }
                        }
                    }
                    case "<=" -> {
                        if(getType(value)==DOUBLE){
                            if(!compareGreaterThan(row, columnIndex, value)){
                                output.add(row.getId(row));
                            }
                        }
                    }
                }
            }
        }
        return output.toString();
    }

    private boolean compareLike(Row row, int index, String value) {
        if(getType(value)!=STRING){
            return false;
        }
        value = value.replace("'", "");
        return row.getValueByColumn(index).contains(value);
    }

    public boolean compareEquals(Row row, int index, String value){
        return row.getValueByColumn(index).equalsIgnoreCase(value);
    }

    public boolean compareGreaterThan(Row row, int index, String value){
        double rowVal = Double.parseDouble(row.getValueByColumn(index));
        double queryVal = Double.parseDouble(value);
        return rowVal>queryVal;
    }

    public boolean compareLessThan(Row row, int index, String value){
        double rowVal = Double.parseDouble(row.getValueByColumn(index));
        double queryVal = Double.parseDouble(value);
        return rowVal<queryVal;
    }

    public ValueType getType(String value){
        switch(value.toLowerCase()){
            case "null" -> {
                return NULL;
            }
            case "true", "false" -> {
                return BOOLEAN;
            }
        }
        if(value.startsWith("'") && value.endsWith("'")) return STRING;
        return DOUBLE;
    }
}
