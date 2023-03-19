package edu.uob;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

public class ParserTests {
    Tokeniser tokeniser;
    ArrayList<Token> tokens;
    Parser parser;

    public void setup(String command) {
        tokeniser = new Tokeniser(command);
        tokens = tokeniser.getTokens();
        parser = new Parser(tokens);
    }


    @Test
    public void useTests(){
        setup("USE name;");
        assertTrue(parser.isParseSuccess());
        setup("USE name");
        assertFalse(parser.isParseSuccess());
        setup("USE;");
        assertFalse(parser.isParseSuccess());
    }

    @Test
    public void createTests(){
        setup("CREATE DATABASE name;");
        assertTrue(parser.isParseSuccess());
        setup("create database name;");
        assertTrue(parser.isParseSuccess());
        setup("create table name;");
        assertTrue(parser.isParseSuccess());
        setup("create table name (value);");
        assertTrue(parser.isParseSuccess());
        setup("create table name (name.value);");
        assertTrue(parser.isParseSuccess());
        setup("create table name (value, name.value);");
        assertTrue(parser.isParseSuccess());
        setup("create table (value, name.value);");
        assertFalse(parser.isParseSuccess());
        setup("create table;");
        assertFalse(parser.isParseSuccess());
        setup("create table table;");
        assertFalse(parser.isParseSuccess());
        setup("create database table;");
        assertFalse(parser.isParseSuccess());
    }

    @Test
    public void dropTests(){
        setup("drop database name;");
        assertTrue(parser.isParseSuccess());
        setup("drop table name;");
        assertTrue(parser.isParseSuccess());
        setup("drop database;");
        assertFalse(parser.isParseSuccess());
        setup("drop table;");
        assertFalse(parser.isParseSuccess());
    }

    @Test
    public void alterTests(){
        setup("alter table name add value;");
        assertTrue(parser.isParseSuccess());
        setup("alter table name add name.value;");
        assertTrue(parser.isParseSuccess());
        setup("alter table name drop value;");
        assertTrue(parser.isParseSuccess());
        setup("alter table name drop name.value;");
        assertTrue(parser.isParseSuccess());
        setup("alter database name add value;");
        assertFalse(parser.isParseSuccess());
        setup("alter table name value;");
        assertFalse(parser.isParseSuccess());
    }

    @Test
    public void insertTests(){
        setup("insert into name values (true);");
        assertTrue(parser.isParseSuccess());
        setup("insert into name values ('value');");
        assertTrue(parser.isParseSuccess());
        setup("insert into name values ('value', 'value');");
        assertTrue(parser.isParseSuccess());
        setup("insert into name values ('value', false);");
        assertTrue(parser.isParseSuccess());
        setup("insert into name values (1.1);");
        assertTrue(parser.isParseSuccess());
        setup("insert into name values (1 .1);");
        assertTrue(parser.isParseSuccess());
        setup("insert into name values (123);");
        assertTrue(parser.isParseSuccess());
        setup("insert into name values (1 2 3);");
        assertTrue(parser.isParseSuccess());
        setup("insert into name values (123);");
        assertTrue(parser.isParseSuccess());
        setup("insert into name values (NULL, true, false, 1 2 3, 1.1 2, 123);");
        assertTrue(parser.isParseSuccess());
        setup("insert into name values (value);");
        assertFalse(parser.isParseSuccess());
    }

    @Test
    public void selectTests(){
        setup("select * from name;");
        assertTrue(parser.isParseSuccess());
        setup("select value from name;");
        assertTrue(parser.isParseSuccess());
        setup("select name.value from name;");
        assertTrue(parser.isParseSuccess());
        setup("select value,value from name;");
        assertTrue(parser.isParseSuccess());
        setup("select value from name where value == false;");
        assertTrue(parser.isParseSuccess());
        setup("select * from name where value == FALSE and (value > 35) and value == 29;");
        assertTrue(parser.isParseSuccess());
        setup("select * from name where (value == FALSE) or (value > 35);");
        assertTrue(parser.isParseSuccess());
        setup("select * from name where (value == FALSE) and value >= 35;");
        assertTrue(parser.isParseSuccess());
        setup("select * from name where (value == FALSE and (value <= 35);");
        assertFalse(parser.isParseSuccess());
        setup("select * from name where (value like FALSE and value > 35);");
        assertTrue(parser.isParseSuccess());
        setup("select * from name where value == FALSE and) (value > 35;");
        assertFalse(parser.isParseSuccess());
        setup("select * from name where value == FALSE and (value > 35;");
        assertFalse(parser.isParseSuccess());
        setup("select value from name where value == value;");
        assertFalse(parser.isParseSuccess());
        setup("select value from name where value == 'value';");
        assertTrue(parser.isParseSuccess());
    }

    @Test
    public void updateTests() {
        setup("update name set name = 'value' where value like 'value';");
        assertTrue(parser.isParseSuccess());
        setup("update name set table = 'value' where value like 'value';");
        assertFalse(parser.isParseSuccess());
        setup("update name set name = 'value' where (value like 'value');");
        assertTrue(parser.isParseSuccess());
    }

    @Test
    public void deleteTests(){
        setup("delete from name where value like 'value';");
        assertTrue(parser.isParseSuccess());
        setup("delete from name where (value > 35);");
        assertTrue(parser.isParseSuccess());
        setup("delete from table where (value > 35);");
        assertFalse(parser.isParseSuccess());
    }

    @Test
    public void joinTests(){
        setup("join table_name and table_name on name and name;");
        assertTrue(parser.isParseSuccess());
        setup("join table and table_name on name and name;");
        assertFalse(parser.isParseSuccess());
        setup("join table_name and table_name on name;");
        assertFalse(parser.isParseSuccess());
    }

}
