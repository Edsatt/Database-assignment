package edu.uob;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DropTableCommand extends DBCommand{

    public DropTableCommand(){
        this.databases = DBServer.databases;
    }

    public void setServer(DBServer server) {
        this.server = server;
    }

    public void setId(String dbName){
        this.id = dbName;
    }

    public void interpretCommand() {
        try{
            server.checkInDatabase();
        } catch(IOException e){
            DBServer.output = ("[ERROR]"+newLine+"Must be Using a database to drop a table");
            return;
        }
        filePath = server.getCurrentFolderPath().concat(File.separator +id.toLowerCase()+".tab");
        try{
            server.fileExists(filePath,true);
        } catch(IOException e){
            DBServer.output = ("[ERROR]"+newLine+"Table "+id+" not found in current database");
            return;
        }
        try {
            Files.delete(Paths.get(filePath));
        } catch(IOException ioe) {
            DBServer.output = ("[ERROR]"+newLine+"Cannot delete folder");
            return;
        }
        server.getDatabase().removeTable(id);
    }


}
