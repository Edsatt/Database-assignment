package edu.uob;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CreateDBCommand extends DBCommand{

    public CreateDBCommand(){
        this.databases = DBServer.databases;
    }

    public void setServer(DBServer server) {
        this.server = server;
    }

    public void setId(String dbName){
        this.id = dbName;
    }

    public void interpretCommand() {
        filePath = server.getStorageFolderPath().concat(File.separator +id.toLowerCase());
        if(!checkFileExists()) return;
        if(!canCreateDirectory()) return;
        database = new Database(id);
        server.addDatabase(id, database);
    }

    public boolean checkFileExists(){
        try{
            server.fileExists(filePath,false);
        } catch(IOException e){
            DBServer.output = ("[ERROR]: Database "+id+" already exists");
            return false;
        }
        return true;
    }

    public boolean canCreateDirectory(){
        try {
            Files.createDirectories(Paths.get(filePath));
        } catch(IOException ioe) {
            DBServer.output = ("[ERROR]: Cannot create folder");
            return false;
        }
        return true;
    }
}
