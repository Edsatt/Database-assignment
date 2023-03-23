package edu.uob;

import java.util.ArrayList;
import java.util.List;
public class Row {
    private final ArrayList<String> values;
    private final String rowName;

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

    public char getId(Row row){
        return row.getValueByColumn(0).charAt(0);
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

    public void removeValue(String value){
        values.remove(value);
    }
}