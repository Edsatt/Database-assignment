package edu.uob;

import java.io.FileNotFoundException;

public class UseCommand extends DBCommand{

    public void setServer(DBServer server){
        this.server = server;
    }

    public void setId(String dbName){
        this.id = dbName;
    }

    public void interpretCommand() {
        server.resetCurrentFolderPath();
        if(!checkFileExists()) return;
        server.setCurrentFolderPath(id);
        database = new Database(id);
        server.setDatabase(database);
    }

    public boolean checkFileExists() {
        try{
            server.findFile(id);
        } catch (FileNotFoundException e) {
            DBServer.output=("[ERROR]: File with name " +id +" not found");
            return false;
        }
        return true;
    }
}
