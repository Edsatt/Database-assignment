package edu.uob;

import java.util.ArrayList;
import java.util.Objects;
import java.util.List;

//Each row has a number and an array list for its cells
public class Row {
    private ArrayList<String> values;
    private String rowName;

    public Row(String rowName){
        this.values = new ArrayList<>();
        this.rowName = rowName;
    }

    public ArrayList<String> getValues() {
        return values;
    }

    public boolean contains(String value){
        for (String s : values) {
            if (Objects.equals(s, value)) {
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

    //finds the column index for a given value. This can be used to get the column name
    public int getColumnIndex(String value){
        for(int i=0; i<values.size(); i++){
            if(Objects.equals(values.get(i), value)){
                return i;
            }
        }
        return values.size();
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
}