package edu.uob;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DropCommand extends DBCommand{

    public DropCommand(){
        this.databases = DBServer.databases;
    }

    public void setServer(DBServer server) {
        this.server = server;
    }

    public void setId(String dbName){
        this.id = dbName;
    }

    public void setCommandType(String commandType){
        this.commandType = commandType;
    }

    public void interpretCommand() {
        switch(commandType){
            case "DATABASE" -> filePath = server.getStorageFolderPath().concat(File.separator +id.toLowerCase());
            case "TABLE" -> {
                filePath = server.getCurrentFolderPath().concat(File.separator +id.toLowerCase()+".tab");
                idFilePath = server.getCurrentFolderPath().concat(File.separator +id.toLowerCase()+"_id.txt");
            }
        }
        try{
            server.fileExists(filePath,true);
        } catch(IOException e){
            DBServer.output = ("[ERROR]"+newLine+" "+commandType+" "+id+" not found");
            return;
        }
        try{
            switch(commandType){
                case "DATABASE" -> {
                    deleteDirContents(filePath);
                    server.removeDatabase(id);
                }
                case "TABLE" -> {
                    server.getDatabase().removeTable(id);
                    Files.delete(Paths.get(idFilePath));
                }
            }
            Files.delete(Paths.get(filePath));
        } catch(IOException ioe) {
            DBServer.output = ("[ERROR]"+newLine+"Cannot delete folder");
        }

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
