package edu.uob;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class FileParser {
    private Table table;
    private String tableName;
    private final ArrayList<Row> rows;

    public FileParser(){
        this.rows = new ArrayList<>();
    }

    public Table fileReader(String filePath) {
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
        setTableName(fileToOpen);
        return createTable();
    }

    public void lineToRow(String fileLine){
        String [] line = fileLine.split("\t");
        ArrayList<String> rowList = new ArrayList<>(Arrays.asList(line));
        Row row = new Row("temp", rowList);
        rows.add(rows.size(), row);
    }

    public void setTableName(File file){
        tableName = file.getName().replace(".tab","");
    }

    public Table createTable(){
        for(int i=0; i<rows.size(); i++){
            if(i==0){
                this.table = new Table(tableName, rows.get(i).getValues());
            }
            if(i>0){
                table.addRow(("row" +i), rows.get(i));
            }
        }
        return table;
    }

}
