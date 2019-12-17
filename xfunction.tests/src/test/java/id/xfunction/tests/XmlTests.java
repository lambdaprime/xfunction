package id.xfunction.tests;

import static java.util.stream.Collectors.joining;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import id.xfunction.Xml;

public class XmlTests {

    @Test
    public void test_query() throws Exception {
        String xml = "<notes><note><to test=\"ggg1\">Tove</to></note><note><to test=\"ggg2\">Bove</to></note></notes>";
        var l = Xml.query(xml, "//note/to/@test");
        assertEquals(2, l.size());
        assertEquals("ggg1 ggg2", l.stream().collect(joining(" ")));
    }

    @Test
    public void test_replace() throws Exception {
        String xml = "<notes><note><to test=\"ggg1\">Tove</to></note><note><to test=\"ggg2\">Bove</to></note></notes>";
        var actual = Xml.replace(xml, "//note/to/@test", "lol");
        String expected = "<notes><note><to test=\"lol\">Tove</to></note><note><to test=\"lol\">Bove</to></note></notes>";
        assertEquals(expected, actual);
    }
}
