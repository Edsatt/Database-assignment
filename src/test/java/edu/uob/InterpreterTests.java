package edu.uob;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class InterpreterTests {
    Tokeniser tokeniser;
    ArrayList<Token> tokens;
    Parser parser;
    DBCommand command;
    DBServer testServer = new DBServer();
    DatabaseList databases;
    Database database;
    Table table;

    String errorStart = ("[ERROR]"+System.lineSeparator());

    public void setup(String query) {
        tokeniser = new Tokeniser(query);
        tokens = tokeniser.getTokens();
        parser = new Parser(tokens);
        parser.outputParseResult();
        command = parser.parseCommand();
        command.setServer(testServer);
        command.interpretCommand();
    }

    @Test
    public void createAndDropTests(){
        setup("create database test;");
        assertEquals(testServer.getOutput(), "[OK]");
        setup("use test;");
        assertEquals(testServer.getOutput(), "[OK]");
        setup("create table test_table;");
        assertEquals(testServer.getOutput(), "[OK]");
        setup("create table test_table2(value1, value2);");
        assertEquals(testServer.getOutput(), "[OK]");
        table = testServer.getDatabase().getTable("test_table2");
        assertEquals(table.getRow("columnNames").getValueByColumn(0), "id");
        assertEquals(table.getRow("columnNames").getValueByColumn(1), "value1");
        setup("drop database test;");
        assertEquals(testServer.getOutput(), "[OK]");
    }

    @Test
    public void useErrorLogTests(){
        setup("use test;");
        assertEquals(testServer.getOutput(), errorStart+"File with name test not found");
    }

    @Test
    public void createErrorLogTests(){
        setup("create database test;");
        setup("create database test;");
        assertEquals(testServer.getOutput(), errorStart+"Database test already exists");
        setup("use test;");
        setup("create table test_table;");
        setup("create table test_table;");
        assertEquals(testServer.getOutput(), errorStart+"Table test_table already exists");
        setup("drop database test");
    }

    @Test
    public void selectTests(){
        setup("create database test;");
        setup("use test");
        setup("create table test1(name, age);");
        setup("insert into test1 values('Ed', 25);");
        setup("insert into test1 values('Peter', 23);");
        setup("insert into test1 values('Isobel', 24);");
//        setup("select age,name from test1 where age == 25 and name like 'el';");
        setup("select test1.age,name from test1 where ((name != 'isobel') or (age >3)) and ((name == 'peter') or (name == 'ed'));");
//        setup("select name from test1 where age > 24;");
//        setup("select name from test1 where age <= 24;");
//        setup("select name from test1 where age < 25;");
//        setup("select name from test1 where age >= 24;");
//        setup("select age from test1 where name like 'e';");
        System.out.println(testServer.getOutput());
        setup("drop database test");

    }


}
