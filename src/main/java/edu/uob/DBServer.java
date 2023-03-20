package edu.uob;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Objects;


/** This class implements the DB server. */
public class DBServer {

    private static final char END_OF_TRANSMISSION = 4;
    private final String storageFolderPath;
    private String currentFolderPath;
    private String newLine = System.lineSeparator();
    Tokeniser tokeniser;
    static ArrayList<Token> tokens;
    static DatabaseList databases;
    private Database database;
    private Table table;
    static String output;

    public static void main(String args[]) throws IOException {
        DBServer server = new DBServer();
        server.blockingListenOn(8888);
    }

    public DBServer() {
        databases = new DatabaseList();
        database = new Database("");
        currentFolderPath = storageFolderPath = Paths.get("databases").toAbsolutePath().toString();
        try {
            Files.createDirectories(Paths.get(storageFolderPath));
        } catch(IOException ioe) {
            System.out.println("Can't seem to create database storage folder " + storageFolderPath);
        }
        fileList(storageFolderPath);
    }

    public String handleCommand(String command) throws Exception {
//        StringBuilder output = new StringBuilder();
//        for(Table table: database.getTables().values()){
//            output.append("Table name: ").append(table.getTableName()).append(newLine);
//            for(Row row: table.getRows().values()){
//                output.append(row.outputRow()).append(newLine);
//            }
//            output.append(newLine);
//        }
//        return output.toString();
        tokeniser = new Tokeniser(command);
        tokens = tokeniser.getTokens();
        Parser parser = new Parser(tokens);
        DBCommand DBCommand = parser.parseCommand();
        parser.outputParseResult();
        if(parser.isParseSuccess()){
            DBCommand.setServer(this);
            DBCommand.interpretCommand();
        }
        return output;
    }

    public void fileList(String path){
        File[] files = new File(path)
                .listFiles((dir, name) -> name.endsWith(".tab"));
        assert files != null;
        for(File file: files) {
            FileParser fileParser = new FileParser();
            fileParser.fileReader(file.getPath());
        }
    }

    public void findFile(String filename) throws FileNotFoundException{
        String filePath = storageFolderPath.concat(File.separator +filename);
        File file = new File(filePath);
        if(!file.exists()) throw new FileNotFoundException("File not found");
    }

    public void fileExists(String filePath, boolean shouldExist) throws IOException{
        File file = new File(filePath);
        if(!shouldExist && file.exists()) throw new IOException("File already exists");
        if(shouldExist && !file.exists()) throw new IOException("File doesn't exist");
    }

    public void setCurrentFolderPath(String filename){
        currentFolderPath = storageFolderPath.concat(File.separator +filename);
    }

    public String getCurrentFolderPath(){
        return currentFolderPath;
    }

    public String getStorageFolderPath(){
        return storageFolderPath;
    }

    public void resetCurrentFolderPath() {
        currentFolderPath = storageFolderPath;
    }

    public void checkInDatabase() throws IOException{
        if(Objects.equals(currentFolderPath, storageFolderPath)){
            throw new IOException("Currently outside of database");
        }
    }

    public void addDatabase(String name, Database database){
        databases.addDatabase(name, database);
    }

    public void removeDatabase(String name){
        if(Objects.equals(this.database.getDatabaseName(), name)){
            setDatabase(new Database(""));
        }
        databases.removeDatabase(name);
    }

    public void setDatabase(Database database){
        this.database = database;
    }

    public Database getDatabase(){
        return database;
    }

    public void setTable(Table table){
        this.table = table;
    }

    public Table getTable(){
        return table;
    }

    //  === Methods below handle networking aspects of the project - you will not need to change these ! ===

    public void blockingListenOn(int portNumber) throws IOException {
        try (ServerSocket s = new ServerSocket(portNumber)) {
            System.out.println("Server listening on port " + portNumber);
            while (!Thread.interrupted()) {
                try {
                    blockingHandleConnection(s);
                } catch (IOException e) {
                    System.err.println("Server encountered a non-fatal IO error:");
                    e.printStackTrace();
                    System.err.println("Continuing...");
                }
            }
        }
    }

    private void blockingHandleConnection(ServerSocket serverSocket) throws IOException {
        try (Socket s = serverSocket.accept();
        BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))) {

            System.out.println("Connection established: " + serverSocket.getInetAddress());
            while (!Thread.interrupted()) {
                String incomingCommand = reader.readLine();
                System.out.println("Received message: " + incomingCommand);
                String result = handleCommand(incomingCommand);
                writer.write(result);
                writer.write("\n" + END_OF_TRANSMISSION + "\n");
                writer.flush();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
