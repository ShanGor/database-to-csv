package tech.comfortheart.util;

import org.junit.Test;

public class EncryptUtilTests {
    @Test
    public void testEncrypt() throws Exception {
        String encrypted = EncryptUtil.encrypt("/Users/sam/git/java/database-to-csv/src/test/resources/demo.cer", "Are you okay");
        System.out.println(encrypted);
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
}
