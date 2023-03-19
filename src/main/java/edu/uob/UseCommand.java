package edu.uob;

public class UseCommand extends DBCommand{

    public UseCommand(){
        this.databases = DBServer.databases;
    }

    @Override
    public void setId(String dbName){
        this.id = dbName;
    }
    @Override
    public void interpretCommand() {
        try{
            databases.searchDatabase(id);
        }catch(DatabaseNotFoundException e){
            DBServer.output=("Database with name " +id +" not found");
        }
        DBServer.database = databases.getDatabase(id);
    }
}
