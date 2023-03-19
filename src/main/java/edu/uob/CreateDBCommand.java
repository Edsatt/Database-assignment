package edu.uob;

public class CreateDBCommand extends DBCommand{

    public CreateDBCommand(){
        this.databases = DBServer.databases;
    }

    @Override
    public void setId(String dbName){
        this.id = dbName;
    }
    @Override
    public void interpretCommand() {
        database = new Database();
        DBServer.databases.addDatabase(id,database);

    }
}
