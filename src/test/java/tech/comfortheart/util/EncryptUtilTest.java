package tech.comfortheart.util;

import org.junit.Test;


import static org.junit.Assert.assertEquals;

public class EncryptUtilTest {
    @Test
    public void testEncrypt() throws Exception {
        String encrypted = EncryptUtil.encrypt("/Users/sam/git/java/database-to-csv/src/test/resources/demo.cer", "Are you okay");
        System.out.println(encrypted);
        new EncryptUtil();
    }

    @Test
    public void testDecrypt() throws Exception {
        String encrypted = "adjYtGwFk1S4KQTYiNTUHD0qLJWMzfjLFthGljTnMYf2lMHMVbBzBtms4GjKjkSMDky1fG/si2ZpGQJaxvGyr766asrGOo4NJ6JxQMbK0YJRmEb6hK8eOYk82Bzff7ItO5wYD9ErH/x29gq0sjoxQc9pLIK4kTiVvtkqEp5kqI2Jc6vgnphkIS0JuoKRQYg+IXgK6x0VniReMDXr29jRjPQjyBoFkHfSYpLO5VPHlqeITb2bq/+g0cnwSXv+Hr8aB8NlBFFTLTJMbe1wZjPAHh4BPm/AZsFtz0aZk8tmdwJHgeazOMu1rVNwt39Ls2s7jng5+iVy4sG0T1PwPiXpew==";

        String decrypted = EncryptUtil.decrypt("/Users/sam/git/java/database-to-csv/src/test/resources/demo.jks",
                "E2E_Alias",
                "changeit",
                "changeit", encrypted);
        assert decrypted.equals("Are you okay");
    }

    @Test
    public void testGenerateKeystore() {
        try {
            String password = EncryptUtil.generateKeystore("comfortheart.tech",
                    "main",
                    "comfortheart",
                    "gz",
                    "gd",
                    "CN",
                    2048,
                    730,
                    "test",
                    "/tmp/test.jks",
                    "/tmp/test.cer");

            String encrypted = EncryptUtil.encrypt("/tmp/test.cer", "Are you okay");

            String decrypted = EncryptUtil.decrypt("/tmp/test.jks",
                    "test",
                    password,
                    password, encrypted);
            assertEquals(decrypted, "Are you okay");
            System.out.println("Tested the decryption and encryption successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGenerateRandomPassword() {
        String str = EncryptUtil.generateRandomPassword(10);
        System.out.println(str);
    }
}
