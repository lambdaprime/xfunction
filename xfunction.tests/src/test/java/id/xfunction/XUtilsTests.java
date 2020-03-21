package id.xfunction;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import id.xfunction.XUtils;
import id.xfunction.function.Unchecked;

public class XUtilsTests {

    int m() throws Exception {
        return 0;
    }

    @Test
    public void testSafe() {
        Unchecked.runUnchecked(this::m);
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void test_infiniteRandomStream() {
        Set s1 = XUtils.infiniteRandomStream(10)
                .limit(5)
                .collect(toSet());
        Set s2 = XUtils.infiniteRandomStream(10)
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

    @Test
    public void test_readResourceAsStream() {
        List<String> actual = XUtils.readResourceAsStream("testFile")
            .collect(toList());
        assertEquals("[line 1, line 2]",
            actual.toString());
    }

    @Test
    public void test_readResourceAsStream_with_class() {
        List<String> actual = XUtils.readResourceAsStream(this.getClass(), "testFile2")
            .collect(toList());
        assertEquals("[line 1, line 2]",
            actual.toString());
    }

    @Test
    public void test_readResource() {
        String actual = XUtils.readResource("testFile");
        assertEquals("line 1\nline 2",
            actual.toString());
    }

    @Test
    public void test_readResource_with_class() {
        String actual = XUtils.readResource(this.getClass(), "testFile2");
        assertEquals("line 1\nline 2",
            actual.toString());
    }

    public static void main(String[] args) {
        XUtils.printMemoryConsumption(100);
        new XUtilsTests().testSafe();
    }
}
