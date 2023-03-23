package edu.uob;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class InterpreterTests {
    Tokeniser tokeniser;
    ArrayList<Token> tokens;
    Parser parser;
    DBCommand command;
    DBServer testServer = new DBServer();
    String output;


    public void setup(String query) {
        tokeniser = new Tokeniser(query);
        tokens = tokeniser.getTokens();
        parser = new Parser(tokens);
        parser.outputParseResult();
        command = parser.parseCommand();
        command.setServer(testServer);
        command.interpretCommand();
        output = testServer.getOutput();
    }

    @Test
    public void useTests(){
        setup("create database test;");
        setup("use test;");
        assertTrue(output.contains("[OK]"));
        setup("drop database test;");
        setup("use test;");
        assertTrue(output.contains("[ERROR]"));
        setup("create database test1;");
        setup("create database test2;");
        setup("use test1;");
        assertEquals(testServer.getDatabase().getDatabaseName(), "test1");
        setup("use test2;");
        assertEquals(testServer.getDatabase().getDatabaseName(), "test2");
        setup("drop database test1;");
        setup("drop database test2;");
    }

    @Test
    public void createDatabaseTests(){
        setup("create database test;");
        assertTrue(output.contains("[OK]"));
        setup("create database test;");
        assertTrue(output.contains("[ERROR]"));
    }

    @Test
    public void createTableTests(){
        setup("create database test;");
        setup("create table test;");
        //Must be within a database to create a table
        assertTrue(output.contains("[ERROR]"));
        setup("use test;");
        setup("create table test;");
        assertTrue(output.contains("[OK]"));
        setup("create table test;");
        assertTrue(output.contains("[ERROR]"));
        setup("create table TEST;");
        assertTrue(output.contains("[ERROR]"));
        assertNull(testServer.getDatabase().getTable("test"));
        setup("drop database test;");
    }

    @Test
    public void createTableValueTests(){
        setup("create database test;");
        setup("use test;");
        setup("create table people(Name, Age);");
        Table testTable = testServer.getDatabase().getTable("people");
        assertNotNull(testTable);
        assertEquals(testTable.getNumCols(),3);
        assertEquals(testTable.getColumnName(0),"id");
        assertEquals(testTable.getColumnName(1),"Name");
        assertEquals(testTable.getColumnName(2),"Age");
        //if a table name is referenced in the attribute list it must match the current table
        setup("create table test(test.name, age;");
        assertTrue(output.contains("[OK]"));
        setup("create table test1(wrongName.value, age;");
        assertTrue(output.contains("[ERROR]"));
        //Cannot create a table with a column called id
        setup("create table test1(id, age);");
        assertTrue(output.contains("[ERROR]"));
        setup("drop database test;");
    }
    
    @Test
    public void dropTests(){
        setup("create database test;");
        String filePath = testServer.getStorageFolderPath();
        File database = new File(filePath+ File.separator+"test");
        assertTrue(database.exists());
        setup("drop database test;");
        assertFalse(database.exists());
        setup("create database test");
        setup("use test;");
        setup("create table table1;");
        setup("create table table2;");
        File table1 = new File(filePath+ File.separator+"test"+File.separator+"table1.tab");
        assertTrue(table1.exists());
        File table2 = new File(filePath+ File.separator+"test"+File.separator+"table2.tab");
        assertTrue(table2.exists());
        setup("drop table table1;");
        assertFalse(table1.exists());
        setup("drop database test;");
        assertFalse(table2.exists());
    }

    @Test
    public void alterAddTests(){
        setup("create database test;");
        setup("use test;");
        setup("create table people(Name, Age);");
        setup("alter table people add Job;");
        Table people = testServer.getDatabase().getTable("people");
        assertEquals(people.getNumCols(), 4);
        assertEquals(people.getColumnName(3),"Job");
        DBServer newServer = new DBServer();
        newServer.handleCommand("use test;");
        newServer.handleCommand("select Job from people;");
        assertTrue(newServer.getOutput().contains("Job"));
        setup("drop database test;");
    }

    @Test
    public void alterDropTests(){
        setup("create database test;");
        setup("use test;");
        setup("create table people(Name, Age, Job);");
        setup("alter table people drop Job;");
        Table people = testServer.getDatabase().getTable("people");
        assertEquals(people.getNumCols(), 3);
        DBServer newServer = new DBServer();
        newServer.handleCommand("use test;");
        newServer.handleCommand("select Job from people;");
        assertTrue(newServer.getOutput().contains("[ERROR]"));
        setup("drop database test;");
    }

    @Test
    public void insertTests(){
        setup("create database test;");
        setup("use test;");
        setup("create table people(Name, Age, Job);");
        setup("insert into people values('Harry', 33, 'Pilot');");
        setup("insert into people values('Bob', 23, 'Teacher');");
        Table people = testServer.getDatabase().getTable("people");
        assertEquals(people.getRow("row 1").getValueByColumn(1),"'Harry'");
        assertEquals(people.getRow("row 2").getValueByColumn(3),"'Teacher'");
        //Inserting too few or too many values into a table returns an error
        setup("insert into people values('Isobel', 24);");
        assertTrue(output.contains("[ERROR]"));
        setup("insert into people values('Isobel', 24, 'Vet', true);");
        assertTrue(output.contains("[ERROR]"));
        setup("drop database test;");
    }

    @Test
    public void selectTests(){
        setup("create database test;");
        setup("use test;");
        setup("create table people(Name, Age, Job);");
        setup("insert into people values('Harry', 33, 'Pilot');");
        setup("insert into people values('Bob', 23, 'Teacher');");
        setup("insert into people values('Isobel', 24, 'Vet');");
        setup("select * from people;");
        assertTrue(output.contains("id"));
        assertTrue(output.contains("Name"));
        assertTrue(output.contains("Age"));
        assertTrue(output.contains("Job"));
        setup("select id from people;");
        assertTrue(output.contains("id"));
        assertFalse(output.contains("Name"));
        assertFalse(output.contains("Age"));
        assertFalse(output.contains("Job"));
        setup("select Name,Age from people;");
        assertFalse(output.contains("id"));
        assertTrue(output.contains("Name"));
        assertTrue(output.contains("Age"));
        assertFalse(output.contains("Job"));
        setup("drop database test;");
    }

    @Test
    public void selectConditionsTests(){
        setup("create database test;");
        setup("use test;");
        setup("create table people(Name, Age, Job, Pet);");
        setup("insert into people values('Harry', 33, 'Pilot', true);");
        setup("insert into people values('Bob', 23, 'Teacher', false);");
        setup("insert into people values('Isobel', 24, 'Vet', true);");
        setup("insert into people values('Ed', 24, 'Student', false);");
        setup("select * from people where name=='isobel';");
        assertTrue(output.contains("Isobel"));
        assertTrue(output.contains("Vet"));
        assertFalse(output.contains("Bob"));
        assertFalse(output.contains("Pilot"));
        setup("select name from people where (pet==true or age<24);");
        assertTrue(output.contains("Isobel"));
        assertTrue(output.contains("Harry"));
        assertTrue(output.contains("Bob"));
        assertFalse(output.contains("Ed"));
        assertFalse(output.contains("Vet"));
        setup("drop database test;");
    }

    @Test
    public void selectReorderColumns(){
        //shows that columns are printed in the order specified by the selection query
        setup("create database test;");
        setup("use test;");
        setup("create table people(Name, Age, Job);");
        setup("insert into people values('Harry', 33, 'Pilot');");
        setup("insert into people values('Bob', 23, 'Teacher');");
        setup("insert into people values('Isobel', 24, 'Vet');");
        setup("select name,id from people;");
        String singleLine = testServer.getOutput().replace("\n"," ").trim();
        assertEquals(singleLine.charAt(singleLine.length()-1), '2');
        setup("drop database test;");
    }

    @Test
    public void nestedCondition(){
        setup("create database test;");
        setup("use test;");
        setup("create table people(Name, Age);");
        setup("insert into people values('Harry',24 );");
        setup("insert into people values('Eve', 17);");
        setup("insert into people values('Isobel' 24);");
        setup("insert into people values('Steve', 24);");
        setup("insert into people values('Larry', 19);");
        setup("select * from people where (((name like 'arry') and (age > 20)) or (name like 've'));");
        assertTrue(output.contains("Steve"));
        assertTrue(output.contains("Harry"));
        assertTrue(output.contains("Eve"));
        assertFalse(output.contains("Larry"));
        setup("drop database test;");
    }

    @Test
    public void updateTests(){
        setup("create database test;");
        setup("use test;");
        setup("create table people(Name, Age, Job);");
        setup("insert into people values('Harry', 33, 'Pilot');");
        setup("insert into people values('Bob', 23, 'Teacher');");
        setup("insert into people values('Isobel', 23, 'Vet');");
        setup("update people set name='fred',job='farmer' where age == 23;");
        Table people = testServer.getDatabase().getTable("people");
        assertEquals(people.getRow("row 2").getValueByColumn(1),"'fred'");
        assertEquals(people.getRow("row 3").getValueByColumn(3),"'farmer'");
        setup("drop database test;");
    }

    @Test
    public void deleteTests(){
        setup("create database test;");
        setup("use test;");
        setup("create table people(Name, Age, Job);");
        setup("insert into people values('Harry', 33, 'Pilot');");
        setup("insert into people values('Bob', 23, 'Teacher');");
        setup("insert into people values('Isobel', 23, 'Vet');");
        Table people = testServer.getDatabase().getTable("people");
        assertEquals(people.getNumRows(), 4);
        setup("delete from people where age == 33 or name == 'isobel';");
        assertEquals(people.getNumRows(), 2);
        setup("insert into people values('Ed', 25, 'Student')");
        //ids are unique, so when a row is and a new row added the id is new
        assertEquals(people.getRow("row 2").getValueByColumn(0), "3");
        setup("drop database test");
    }

    @Test
    public void JoinTests(){
        setup("create database test;");
        setup("use test;");
        setup("create table test1(name, age);");
        setup("insert into test1 values('ed', 25)");
        setup("insert into test1 values('isobel', 24)");
        setup("insert into test1 values('peter', 25)");
        setup("create table test2(number, job);");
        setup("insert into test2 values(1, 'vet');");
        setup("insert into test2 values(0, 'student');");
        setup("insert into test2 values(2, 'analyst');");
        setup("join test1 and test2 on id and number;");
        String singleLine = output.replace("\n"," ").trim();
        String[] tokens = singleLine.split(" ");
        String[] columnNames = tokens[0].split("\t");
        assertEquals(columnNames[0], "id");
        assertEquals(columnNames[1], "name");
        assertEquals(columnNames[2], "age");
        assertTrue(columnNames[3].contains("job"));
        setup("drop database test");
    }

}
