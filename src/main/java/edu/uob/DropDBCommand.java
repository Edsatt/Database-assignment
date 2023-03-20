package edu.uob;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DropDBCommand extends DBCommand{

    public DropDBCommand(){
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
        try{
            server.fileExists(filePath,true);
        } catch(IOException e){
            DBServer.output = ("[ERROR]"+newLine+"Table "+id+" not found");
        }
        deleteDirContents(filePath);
        try {
            Files.delete(Paths.get(filePath));
        } catch(IOException ioe) {
            DBServer.output = ("[ERROR]"+newLine+"Cannot delete folder");
        }
        server.removeDatabase(id);
    }

    public void deleteDirContents(String filePath){
        File[] files = new File(filePath).listFiles();
        assert files != null;
        for(File file: files) {
            try {
                Files.delete(file.toPath());
            } catch(IOException ioe) {
                DBServer.output = ("[ERROR]"+newLine+"Cannot delete file " +file.getName());
            }
        }
    }
}
