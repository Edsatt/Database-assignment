package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class UnitTests {

    private Database database;
    private Table table;

    // Create a new server _before_ every @Test
    @BeforeEach
    public void setup() {
        this.database = new Database();
        this.table = new Table();
    }
    @Test
    public void makeBasicTable() {
        List<String> columns = Stream.of("Column1", "Column2")
                .collect(Collectors.toList());
        List<String> row1Values = Stream.of("cell1", "cell3")
                .collect(Collectors.toList());
        List<String> row2Values = Stream.of("cell2", "cell4")
                .collect(Collectors.toList());
        Row columnNames = new Row("columnNames");
        Row row1 = new Row("row1");
        Row row2 = new Row("row2");
        table.addRow("columnNames",columnNames);
        table.addRow("row1", row1);
        table.addRow("row2", row2);
        table.addColumnList(columns);
        row1.addValueList(row1Values);
        row2.addValueList(row2Values);
        database.addTable("test_table", table);
        table.outputTable("test_table");
        Query query = new Query(table);
        assertEquals(table.getNumCols(), 2);
        assertEquals(table.getNumRows(), 3);
        assertEquals(table.getColumnName(0),"Column1");
        assertEquals(table.getColumnName(1),"Column2");
        assertEquals(table.getRow("row1").getValueByColumn(0), "cell1");
        assertEquals(table.getRow("row1").getValueByColumn(1), "cell3");
        assertEquals(table.getRow("row2").getValueByColumn(0), "cell2");
        assertEquals(table.getRow("row2").getValueByColumn(1), "cell4");
        assertEquals(query.getColumnNameFromValue("cell1"),"Column1");
    }
}

