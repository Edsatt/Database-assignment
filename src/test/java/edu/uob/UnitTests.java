package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class UnitTests {

    private Database database;
    private Table table;

    private String storageFolderPath;

    // Create a new server _before_ every @Test
    @BeforeEach
    public void setup() {
        this.database = new Database();
        storageFolderPath = Paths.get("databases").toAbsolutePath().toString();
    }
    @Test
    public void makeBasicTable() {
        this.table = new Table(Stream.of("Column1", "Column2")
                .collect(Collectors.toList()));
        Row row1 = new Row("row1", Stream.of("cell1", "cell3")
                .collect(Collectors.toList()));
        Row row2 = new Row("row2", Stream.of("cell2", "cell4")
                .collect(Collectors.toList()));

        table.addRow("row1", row1);
        table.addRow("row2", row2);
        database.addTable("test_table", table);
        table.outputTable(storageFolderPath,"test_table");

        assertEquals(this.table.getNumCols(), 2);
        assertEquals(table.getNumRows(), 3);
        assertEquals(table.getColumnName(0),"Column1");
        assertEquals(table.getColumnName(1),"Column2");
        assertEquals(table.getRow("row1").getValueByColumn(0), "cell1");
        assertEquals(table.getRow("row1").getValueByColumn(1), "cell3");
        assertEquals(table.getRow("row2").getValueByColumn(0), "cell2");
        assertEquals(table.getRow("row2").getValueByColumn(1), "cell4");
    }
}

