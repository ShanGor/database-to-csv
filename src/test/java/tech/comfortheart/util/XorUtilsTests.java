package tech.comfortheart.util;

import org.junit.Test;

import static tech.comfortheart.util.XorUtils.XOR;


public class XorUtilsTests {
    @Test
    public void testBitSet()
    {
        String psw = "Hello";
        String es = XOR(psw, "key");
        System.out.println(es);
        System.out.println(XOR(es, "key"));
    }
}
