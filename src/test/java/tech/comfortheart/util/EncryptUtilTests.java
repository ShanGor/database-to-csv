package tech.comfortheart.util;

import org.junit.Test;

public class EncryptUtilTests {
    @Test
    public void testEncrypt() throws Exception {
        String encrypted = EncryptUtil.encrypt("/Users/sam/git/java/database-to-csv/src/main/resources/demo.cer", "Are you okay");
        System.out.println(encrypted);
    }

    @Test
    public void testDecrypt() throws Exception {
        String encrypted = "BhBDy9LDwT2/G6co6E0kMzvKAKMeWEngB58ztRG/OjRKBn29gelJvNZOG69AcNI6Iyf6318kAOD/drY/RdNMVRK3SfR3pVf8UCHvRwji0S3/O++ziT1e8xPbXT68JgO+0T1DrSIXUdhxOHxhx+3Klta/wtGaiPQ3+XQrLW0EH3rRv8vbZy0a1Fr1PG7sSz9YVZWQffncUeCHIXH525pD83r3+MLlqUS2pAJXR5Qy1hfHaHmlH4l/on/JB/J5IjGyl7WpwCJXOJKuQKGRVS7YHljLSw9CC87oqahbXmZk7kmyKq3nFwFQPTtYm/sMj14EgPOh/RWE17k/g4+aM1yUiA==";

        String decrypted = EncryptUtil.decrypt("/Users/sam/git/java/database-to-csv/src/main/resources/demo.jks",
                "E2E_Alias",
                "changeit",
                "changeit", encrypted);
        System.out.println(decrypted);
    }
}
