package edu.uob;

import org.junit.jupiter.api.BeforeEach;
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

}
