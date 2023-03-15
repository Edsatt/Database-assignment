package edu.uob;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.ArrayList;

public class FileParser {
    private Table table;
    private String tableName;
    private ArrayList<Row> rows;

    public FileParser(){
        this.table = new Table();
        this.rows = new ArrayList<>();
    }

    public void fileReader(String filePath) {
        File fileToOpen = new File(filePath);
        try{
            FileReader reader = new FileReader(fileToOpen);
            BufferedReader buffReader = new BufferedReader(reader);
            String line;
            while((line = buffReader.readLine()) != null){
                lineToRow(line);
            }
            buffReader.close();
        } catch(IOException ioe){
            System.out.println("Cannot open file");
        }
        createTable();
        setTableName(fileToOpen);
        saveTable();
    }

    public void lineToRow(@NotNull String fileLine){
        Row row = new Row("temp");
        String [] line = fileLine.split("\t");
        for (String value: line) {
            row.addValue(row.getNumValues(), value);
        }
        rows.add(rows.size(), row);
    }

    public void createTable(){
        for(int i=0; i<rows.size(); i++){
            if(i==0){
                table.addRow("columnNames", rows.get(i));
            }
            if(i>0){
                table.addRow(("row" +i), rows.get(i));
            }
        }
    }

    public void setTableName(File file){
        tableName = file.getName().replace(".tab","");
    }

    public void saveTable(){
        DBServer.database.addTable(tableName, table);
    }

}
