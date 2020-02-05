# Database to CSV conversion
The mission of this project, is to create a generic solution to extract database table, to be a CSV. Can run in limited memory, without OOM.

#### Currently supporting below database types
- MySQL (Value as MySQL)
- PostgresQL (Value as "POSTGRESQL")
- Oracle (Value as "ORACLE", Not yet test for performance)
- MS SQL Server (Value as "SQLSERVER")

#### Details please ref to the test cases
```
    @Test
    public void testSqlServerConvert() throws SQLException, IOException {

        DatabaseTableToCSV converter = new DatabaseTableToCSV(mssserverUrl,
                "SqlServer",
                mssserverUsr,
                mssserverPsw);
        try {
            converter.convertTable("test", "/tmp/test.csv");
            converter.convertWithCustomSql("select * from test where id <= 100001", "/tmp/test1.csv");
        } finally {
            converter.close();
        }
    }
```
