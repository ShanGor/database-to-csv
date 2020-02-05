package tech.comfortheart.util;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

/**
 * CSV Writer.
 * No need to flush, it will flush automatically.
 */
public class CsvWriter {
    public static final int BUFFER_SIZE = 1000;

    CSVFormat formatter = CSVFormat.DEFAULT;
    FileWriter fileWriter;
    CSVPrinter printer;

    int rowCounter;

    public CsvWriter(String filePath) throws IOException {
        fileWriter=new FileWriter(filePath);
        printer = new CSVPrinter(fileWriter, formatter);
        rowCounter=0;
    }
    public CsvWriter(String filePath, String[] headers) throws IOException {
        this(filePath);
        writeRecord(headers);
        rowCounter++;
        if (rowCounter >= BUFFER_SIZE) {
            rowCounter=0;
            printer.flush();
        }
    }

    public void writeRecord(String[] row) throws IOException {
        printer.printRecord(row);
    }

    public void close() {

        try {
            printer.flush();
            printer.close();
            fileWriter.close();
        } catch (IOException e) {
        }
    }

    public void writeRecords(List<String[]> data) throws IOException {
        if (data == null || data.isEmpty()) {
            return;
        }

        for (String[] lineData : data) {
            writeRecord(lineData);
        }
    }

    public void writeResultSetWithHeader(ResultSet rs) throws SQLException, IOException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        String[] rowData = new String[columnCount];
        for(int i=1; i<=columnCount; i++) {
            String columnName = metaData.getColumnName(i);
            rowData[i-1] = columnName;
        }

        writeRecord(rowData);

        writeResultSetWithOutHeader(rs, metaData, columnCount);
    }

    private void writeResultSetWithOutHeader(ResultSet rs, ResultSetMetaData metaData, int columnCount) throws SQLException, IOException {
        while (rs.next()) {
            String[] rowData = new String[columnCount];
            for(int i=1; i<=columnCount; i++) {
                int type = metaData.getColumnType(i);
                String columnValue;
                switch (type) {
                    case Types.FLOAT:
                    case Types.INTEGER:
                    case Types.DOUBLE:
                    case Types.BOOLEAN:
                        columnValue = String.valueOf(rs.getFloat(i));
                        break;
                    case Types.DECIMAL:
                        columnValue = rs.getBigDecimal(i).toPlainString();
                        break;
                    case Types.CHAR:
                        columnValue = rs.getString(i);
                        break;
                    case Types.DATE:
                        columnValue = rs.getDate(i).toString();
                        break;
                    default:
                        columnValue = rs.getObject(i).toString();
                        break;
                }
                rowData[i-1] = columnValue;
            }

            writeRecord(rowData);
        }
    }

    public void writeResultSetWithOutHeader(ResultSet rs) throws SQLException, IOException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        writeResultSetWithOutHeader(rs, metaData, columnCount);
    }
}
