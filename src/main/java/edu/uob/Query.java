package edu.uob;

import java.util.HashMap;

public class Query {
    private Table table;
    private HashMap<String, Row> rows;
    public Query(Table table){
        this.table = table;
        rows = table.getRows();
    }

    public String getColumnNameFromValue(String value){
        int columnIndex = 0;
        for (Row row: rows.values()) {
            columnIndex = row.getColumnIndex(value);
            if (columnIndex < row.getNumValues()) break;
        }
        return table.getColumnName(columnIndex);
    }

    public void selectValues(String conditionType, String condition){
        for (Row row: rows.values()) {
            switch (conditionType) {
                case "value" -> {
                    if (row.contains(condition)) {
                        System.out.println(condition);
                    }
                }
                case "row" -> {
                    if (row.contains(condition)) {
                        System.out.println(row.getValues());
                    }
                }
                case "column" -> {
                    int columnIndex = table.getColumnIndex(condition);
                    if(columnIndex< row.getNumValues()){
                        System.out.println(row.getValueByColumn(columnIndex));
                    }
                }
            }
        }
    }
}
