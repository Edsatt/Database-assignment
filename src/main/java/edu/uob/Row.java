package edu.uob;

import java.util.ArrayList;
import java.util.Objects;
import java.util.List;

//Each row has a number and an array list for its cells
public class Row {
    private ArrayList<String> values;
    private String rowName;

    public Row(String rowName, List<String> values){
        this.values = new ArrayList<>();
        this.values.addAll(values);
        this.rowName = rowName;
    }

    public ArrayList<String> getValues() {
        return values;
    }

    public boolean contains(String value){
        for (String s : values) {
            if (s.equalsIgnoreCase(value)){
                return true;
            }
        }
        return false;
    }

    public String getValueByColumn(int colIndex){
        return values.get(colIndex);
    }

    public String getRowName(){
        return this.rowName;
    }

    public int getNumValues(){
        return values.size();
    }

    public int getColumnIndex(String query){
        for(int i=0; i<values.size(); i++){
            if(values.get(i).equalsIgnoreCase(query)){
                return i;
            }
        }
        return -1;
    }

    public void addValue(int index, String value){
        values.add(index, value);
    }

    public void addValueList(List<String> values){
        this.values.addAll(values);
    }
    public void setValue(int index, String value){
        values.set(index, value);
    }

    public void setRowName(String rowName){
        this.rowName = rowName;
    }

    public void removeValue(String value){
        values.remove(value);
    }

    public String outputRow(){
        return String.join("\t", getValues());
    }
}