package edu.uob;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.ArrayList;


/** This class implements the DB server. */
public class DBServer {

    private static final char END_OF_TRANSMISSION = 4;
    private static String storageFolderPath;
    private String newLine = System.lineSeparator();
    private FileParser fileParser;
    Tokeniser tokeniser;
    static ArrayList<Token> tokens;
    private Parser parser;
    static DatabaseList databases;

    public static void main(String args[]) throws IOException {
        DBServer server = new DBServer();
        server.blockingListenOn(8888);
    }

    public DBServer() {
        databases = new DatabaseList();
        storageFolderPath = Paths.get("databases").toAbsolutePath().toString();
        try {
            Files.createDirectories(Paths.get(storageFolderPath));
        } catch(IOException ioe) {
            System.out.println("Can't seem to create database storage folder " + storageFolderPath);
        }
        fileList(storageFolderPath);
    }

    public void fileList(String path){
        File[] files = new File(path)
                .listFiles((dir, name) -> name.endsWith(".tab"));
        assert files != null;
        for(File file: files) {
            this.fileParser = new FileParser();
            fileParser.fileReader(file.getPath());
        }
    }

    public static String getStorageFolderPath() {
        return storageFolderPath;
    }

    public String handleCommand(String command) {
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
        parser = new Parser(tokens);
        return "tokenised";
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
        }
    }
}
