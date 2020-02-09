package tech.comfortheart.util;

import org.junit.Test;

public class DatabaseConfigTests {
    @Test
    public void testCustomizeSql() {
        DatabaseConfig config = new DatabaseConfig();
        config.setTables("user, test");
        config.customizeSql("user ", "select * from user");

        System.out.println(config.getTables().get(0).getCustomSql());
        assert "select * from user".equals(config.getTables().get(0).getCustomSql());

        assert config.getTables().get(1).getCustomSql() == null;
        config.customizeSql("test ", "select * from test");

        assert "select * from user".equals(config.getTables().get(0).getCustomSql());
        assert "select * from test".equals(config.getTables().get(1).getCustomSql());
    }

    @Test
    public void testMassageVariables() {
        String testStr = "i love $today and $yesterday and $tomorrow, today is '$today'";
        String res = DatabaseConfig.massageVariables(testStr);
        System.out.println(res);
    }
}
