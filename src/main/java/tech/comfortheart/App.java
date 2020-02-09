package tech.comfortheart;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import tech.comfortheart.util.DatabaseConfig;
import tech.comfortheart.util.DatabaseTableToCSV;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * App to extract tables from a database to be csv files, store to a folder.
 *
 */
public class App {
    private static Logger logger = Logger.getLogger(App.class.getSimpleName());
    private static final int PROCESS_THREADS = 10;

    public static void main( String[] args ) {
        if (args.length != 1) {
            String path = App.class.getResource(App.class.getSimpleName() + ".class").getFile();
            if (path.startsWith("file:/")) {
                path = path.substring("file:/".length(), path.lastIndexOf('!'));
                logger.info( "Usage: java -jar " + path + " config.xslx");
            } else {
                path = App.class.getName();
                logger.info( "Usage: java " + path  + " config.xslx");
            }
        } else {
            File configFile = new File(args[0]);
            if (!configFile.exists()) {
                logger.severe("Config file does not exist: " + configFile);
            } else {
                process(configFile);
            }
        }
    }

    public static void process(File configFile) {
        long startTime = System.currentTimeMillis();
        try (Workbook wb = WorkbookFactory.create(configFile)){
            Sheet sheet = wb.getSheet("config");
            DatabaseConfig config = new DatabaseConfig();
            sheet.forEach(row -> {
                Cell keyCell = row.getCell(0);
                if (keyCell!= null) {
                    Cell valueCell = row.getCell(1);
                    String key = keyCell.getStringCellValue();
                    String value = valueCell.getStringCellValue();
                    if (DatabaseConfig.notEmpty(key) && DatabaseConfig.notEmpty(value)) {
                        config.setVariable(key, value);
                    }
                }
            });

            ExecutorService executorService = Executors.newFixedThreadPool(PROCESS_THREADS);

            CountDownLatch latch = new CountDownLatch(config.getTables().size());

            for(DatabaseConfig.Table table : config.getTables()) {
                executorService.execute(() -> {
                    logger.info("== Starting to extract table: " + table.getTableName());
                    try(DatabaseTableToCSV databaseTableToCSV = new DatabaseTableToCSV(config)) {

                        File path = new File(config.getCsvLocation());
                        if (!path.exists()) {
                            path.mkdir();
                        }
                        final File csvFile = new File(path, table.getTableName() + ".csv");

                        if (table.getCustomSql() != null) {
                            databaseTableToCSV.convertWithCustomSql(table.getCustomSql(), csvFile.getAbsolutePath());
                        } else {
                            databaseTableToCSV.convertTable(table.getTableName(), csvFile.getAbsolutePath());
                        }
                        logger.info("== table " + table.getTableName() + " is extracted successfully!");
                    } catch (SQLException|IOException e) {
                        logger.info("FATAL ERROR: table " + table.getTableName() + " failed to extract due to " + e.getMessage());
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await();

            long endTime = System.currentTimeMillis();
            executorService.shutdown();
            logger.info("Mission completed in " + (endTime - startTime)/1000.0 + " seconds, please check log for failed/successful cases!");

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
