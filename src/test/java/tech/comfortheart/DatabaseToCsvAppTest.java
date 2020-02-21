package tech.comfortheart;

import org.junit.Test;
import tech.comfortheart.util.EncryptUtil;
import tech.comfortheart.util.IOUtils;
import tech.comfortheart.util.KeystorePasswordUtil;
import tech.comfortheart.util.SupportedDatabase;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for simple App.
 */
public class DatabaseToCsvAppTest
{
    Logger logger = Logger.getLogger("MainTests");
    /**
     * Rigorous Test :-)
     */
    @Test
    public void testStupidThings() {
        SupportedDatabase.DatabaseType.valueOf("MYSQL");
        SupportedDatabase.DatabaseType.values();
        System.out.println(SupportedDatabase.DatabaseType.MYSQL);
        System.out.println(SupportedDatabase.DatabaseType.SQLSERVER);
        System.out.println(SupportedDatabase.DatabaseType.ORACLE);
        System.out.println(SupportedDatabase.DatabaseType.POSTGRESQL);
        logger.info(SupportedDatabase.MYSQL);
        logger.info(SupportedDatabase.POSTGRESQL);
        logger.info(SupportedDatabase.SQL_SQLSERVER);
        logger.info(SupportedDatabase.ORACLE);
        new SupportedDatabase();
    }

    @Test
    public void testKeystoreAndCert() {
        DatabaseToCsvApp.KeystoreAndCert keystoreAndCert = new DatabaseToCsvApp.KeystoreAndCert("my_keystorePath", "myCertPath", "hey");
        assert keystoreAndCert.getPassword().equals("hey");
        assert keystoreAndCert.getCertPath().equals("myCertPath");
        assert keystoreAndCert.getKeystorePath().equals("my_keystorePath");
    }

    @Test
    public void testInitKeyStore() throws Exception {
        String folderPath = "/tmp/fhdipafdkja324324sfldsh";
        System.setProperty("user.home", folderPath);
        File folder = new File(folderPath);
        folder.mkdir();

        StringBuilder sb = new StringBuilder();
        sb.append("myCommonName").append("\r")
                .append("myOU").append("\r")
                .append("myOrg").append("\r")
                .append("myCity").append("\r")
                .append("myState").append("\r")
                .append("myCountry").append("\r");

        InputStream ins = new ByteArrayInputStream(sb.toString().getBytes());
        System.setIn(ins);


        DatabaseToCsvApp.initKeystore("hey");

        String keystorePath = DatabaseToCsvApp.getKeystoreAndCert().getKeystorePath();
        String certPath = DatabaseToCsvApp.getKeystoreAndCert().getCertPath();
        assertEquals("/tmp/fhdipafdkja324324sfldsh/hey/db2csv.jks", keystorePath);
        assert certPath.equals("/tmp/fhdipafdkja324324sfldsh/hey/db2csv.cer");
        String password = DatabaseToCsvApp.getKeystoreAndCert().getPassword();
        assertEquals(password, new KeystorePasswordUtil(new File(folder, "hey")).retrieveKeystorePassword());

        System.setIn(System.in);

        IOUtils.removeFileOrDir(folder);
    }
}
