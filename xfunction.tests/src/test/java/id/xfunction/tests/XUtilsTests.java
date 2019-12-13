package id.xfunction.tests;

import static java.util.stream.Collectors.toSet;
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

    public static void main(String[] args) {
        XUtils.printMemoryConsumption(100);
        new XUtilsTests().testSafe();
        
    }
}
