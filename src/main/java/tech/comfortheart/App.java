package tech.comfortheart;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import tech.comfortheart.util.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
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
    private static final String APP_ID_FILE = ".database_to_csv_app_id";
    private static final String KEYSTORE_FILE_NAME = "db2csv.jks";
    private static final String CERT_FILENAME = "db2csv.cer";
    public static final String ALIAS_NAME = "db2csv";

    public static void main( String[] args ) throws Exception {
        if (args.length != 1 && args.length != 2) {
            String path = App.class.getResource(App.class.getSimpleName() + ".class").getFile();
            if (path.startsWith("file:")) {
                path = path.substring("file:".length(), path.lastIndexOf('!'));
                logger.info( "Usages:");
                System.out.println( "     1: java -jar " + path + " config.xslx");
                System.out.println( "     2: java -jar " + path  + " -init <app_id>");
                System.out.println( "     3: java -jar " + path  + " -encrypt your_password");
            } else {
                path = App.class.getName();
                logger.info( "Usages:");
                System.out.println( "     1: java " + path  + " config.xslx");
                System.out.println( "     2: java " + path  + " -init <app_id>");
                System.out.println( "     3: java " + path  + " -encrypt your_password");
            }
        } else if (args.length == 1){
            File configFile = new File(args[0]);
            if (!configFile.exists()) {
                logger.severe("Config file does not exist: " + configFile);
            } else {
                process(configFile);
            }
        } else if (args.length == 2){
            if (args[0].trim().toLowerCase().equals("-encrypt")) {
                KeystoreAndCert keyInfo = App.getKeystoreAndCert();
                System.out.println("Encrypted as: " + EncryptUtil.encrypt(keyInfo.getCertPath(), args[1].trim()));
            } else if (args[0].trim().toLowerCase().equals("-init")) {
                initKeystore(args[1].trim());
            } else {
                logger.severe("Unknown parameters: " + args[0]);
            }
        }
    }

    /**
     * Process the config file;
     * @param configFile
     */
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

    public static void initKeystore(String appId) throws Exception {
        File home = new File(System.getProperty("user.home"));
        File appIdFile = new File(home, APP_ID_FILE);
        logger.info("AppId file is: " + appIdFile.getAbsolutePath());
        if (appIdFile.exists()) {
            appIdFile.delete();
        }
        logger.info("Writing appId info to the file..");
        Files.write(appIdFile.toPath(), appId.getBytes(), StandardOpenOption.CREATE);
        logger.info("Written!");

        File folder = new File(home, appId);
        if (folder.exists()) {
            tech.comfortheart.util.IOUtils.removeFileOrDir(folder);
        }
        folder.mkdir();

        String commonName = IOUtils.readStandardInput("Common Name: ");
        String organizationUnit = IOUtils.readStandardInput("Organization Unit: ");
        String organization = IOUtils.readStandardInput("Organization: ");
        String city = IOUtils.readStandardInput("City: ");
        String state = IOUtils.readStandardInput("State: ");
        String country = IOUtils.readStandardInput("Country: ");
        int keySize = 2048;
        int validity = 730; // Expire after 2 years, however it can still be used.
        String alias = ALIAS_NAME;
        File keyStorePath = new File(folder, KEYSTORE_FILE_NAME);
        File certPath = new File(folder, CERT_FILENAME);

        logger.info(commonName);

        String password = EncryptUtil.generateKeystore(commonName,
                organizationUnit,
                organization,
                city,
                state,
                country,
                keySize,
                validity,
                alias,
                keyStorePath.getAbsolutePath(),
                certPath.getAbsolutePath());
        KeystorePasswordUtil keystorePasswordUtil = new KeystorePasswordUtil(folder);
        keystorePasswordUtil.saveEncryptedStorePassword(password);
    }

    /**
     * Get the keystore password
     * @return
     */
    public static final KeystoreAndCert getKeystoreAndCert() throws Exception{
        File home = new File(System.getProperty("user.home"));
        File appIdFile = new File(home, APP_ID_FILE);
        logger.info("AppId file is: " + appIdFile.getAbsolutePath());
        if (!appIdFile.exists()) {
            throw new RuntimeException("Your application is not initialized! the AppId file not found!");
        }

        String appId = Files.readAllLines(appIdFile.toPath()).get(0).trim();
        File folder = new File(home, appId);
        if (folder.exists()) {KeystorePasswordUtil keystorePasswordUtil = new KeystorePasswordUtil(folder);
            File keyStorePath = new File(folder, KEYSTORE_FILE_NAME);
            File certPath = new File(folder, CERT_FILENAME);
            String password = keystorePasswordUtil.retrieveKeystorePassword();
            return new KeystoreAndCert(keyStorePath.getAbsolutePath(), certPath.getAbsolutePath(), password);
        } else {
            throw new RuntimeException("Your application is not initialized! the AppId folder not found!");
        }

    }

    public static class KeystoreAndCert {
        private String keystorePath;
        private String certPath;
        private String password;
        public KeystoreAndCert(String keystorePath, String certPath, String password) {
            setCertPath(certPath);
            setKeystorePath(keystorePath);
            setPassword(password);
        }

        public String getKeystorePath() {
            return keystorePath;
        }

        public void setKeystorePath(String keystorePath) {
            this.keystorePath = keystorePath;
        }

        public String getCertPath() {
            return certPath;
        }

        public void setCertPath(String certPath) {
            this.certPath = certPath;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
