package id.xfunction;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import id.xfunction.XUtils;
import id.xfunction.function.Unchecked;

public class XUtilsTests {

    int m() throws Exception {
        return 0;
    }

    @Test
    public void testSafe() {
        Unchecked.get(this::m);
    }

    @Test
    public void test_infiniteRandomStream() {
        Set<String> s1 = XUtils.infiniteRandomStream(10)
                .limit(5)
                .collect(toSet());
        Set<String> s2 = XUtils.infiniteRandomStream(10)
                .limit(5)
                .collect(toSet());
        assertNotEquals(s1, s2);
    }

    @Test
    public void test_infiniteRandomStream_limit_1() {
        Set<String> s1 = XUtils.infiniteRandomStream(10)
                .limit(1)
                .collect(toSet());
        assertEquals(1, s1.size());
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
    public void test_md5Sum_file() throws Exception {
        Path file = Files.createTempFile("gg", "");
        Files.writeString(file, "very long string".repeat(100));
        assertEquals("05ee552a62cdfeb6b21ac40f401c5eed",
            XUtils.md5Sum(file.toFile()));
        file.toFile().delete();
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

    @Test
    public void test_quote() throws Exception {
        assertEquals("\"ggg\"", XUtils.quote("\"ggg\""));
        assertEquals("\"\"", XUtils.quote(""));
        assertEquals(" \"", XUtils.quote(" \""));
        assertEquals("  \"\"", XUtils.quote("  \"\""));
        assertEquals("\"sdsdsd  ", XUtils.quote("\"sdsdsd  "));
        assertEquals("\"sdsdsd", XUtils.quote("\"sdsdsd"));
        assertEquals("\"sd\"", XUtils.quote("sd  "));
        assertEquals("\"sd\"", XUtils.quote("  sd  "));
        assertEquals("  \"sd\"  ", XUtils.quote("  \"sd\"  "));
    }

    @Test
    public void test_unquote() throws Exception {
        assertEquals("ggg", XUtils.unquote("\"ggg\""));
        assertEquals("", XUtils.unquote(""));
        assertEquals(" \"", XUtils.unquote(" \""));
        assertEquals("", XUtils.unquote("  \"\""));
        assertEquals("\"sdsdsd  ", XUtils.unquote("\"sdsdsd  "));
        assertEquals("\"sdsdsd", XUtils.unquote("\"sdsdsd"));
        assertEquals("sd", XUtils.unquote("\"sd\"  "));
        assertEquals("sd", XUtils.unquote("  \"sd\"  "));
        assertEquals("  sd  ", XUtils.unquote("  sd  "));
    }

    @Test
    public void test_throwRuntime() {
        RuntimeException ex = Assertions.assertThrows(RuntimeException.class, () -> 
            XUtils.throwRuntime("msg %s %d", "a1", 3));
        assertEquals("msg a1 3", ex.getMessage());
    }
    
    public static void main(String[] args) {
        XUtils.printMemoryConsumption(100);
        new XUtilsTests().testSafe();
        
        byte[] b = "test".getBytes();
        System.out.println("fff");
        System.out.println(Arrays.toString(b));
    }
}
