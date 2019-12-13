package id.xfunction.tests;

import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import id.xfunction.Unchecked;
import id.xfunction.XUtils;

public class XUtilsTests {

    int m() throws Exception {
        return 0;
    }

    @Test
    public void testSafe() {
        Unchecked.runUnchecked(this::m);
    }

    @Test
    public void test_infiniteRandomStream() {
        var s1 = XUtils.infiniteRandomStream(10)
                .limit(5)
                .collect(toSet());
        var s2 = XUtils.infiniteRandomStream(10)
                .limit(5)
                .collect(toSet());
        assertNotEquals(s1, s2);
    }

    @Test
    public void test_md5Sum() throws Exception {
        assertEquals("F368382E27B21F0D6DB6693DEBE94A14",
            XUtils.md5Sum("I can see what you want").toUpperCase());
        assertEquals("2DDAFC008B12810D7148E3E6943598AC",
            XUtils.md5Sum("A state of trance").toUpperCase());
        assertEquals("31CF05BEC43849BED31296557B064071",
            XUtils.md5Sum("until the sky falls down").toUpperCase());
    }

    public static void main(String[] args) {
        XUtils.printMemoryConsumption(100);
        new XUtilsTests().testSafe();
        
    }
}
