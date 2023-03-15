package edu.uob;

//Each key is a relationship between two tables
public class Key {
    private String foreignTableName;
    private String foreignColumnName;


    public Key(String foreignTableName, String foreignColumnName){
        this.foreignTableName = foreignTableName;
        this.foreignColumnName = foreignColumnName;
    }

    public String getForeignTableName() {
        return this.foreignTableName;
    }

    public String getForeignColumnName() {
        return foreignColumnName;
    }

    public void setForeignTableName(String foreignTableName) {
        this.foreignTableName = foreignTableName;
    }

    public void setForeignColumnName(String foreignColumnName) {
        this.foreignColumnName = foreignColumnName;
    }
}
