/*
 * Copyright 2022 lambdaprime
 * 
 * Website: https://github.com/lambdaprime/xfunction
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package id.xfunction.tests;

import static java.util.stream.Collectors.joining;
import static org.junit.jupiter.api.Assertions.assertEquals;

import id.xfunction.Xml;
import java.util.List;
import org.junit.jupiter.api.Test;

public class XmlTests {

    @Test
    public void test_query() throws Exception {
        String xml =
                "<notes><note><to test=\"ggg1\">Tove</to></note><note><to"
                        + " test=\"ggg2\">Bove</to></note></notes>";
        List<String> l = Xml.query(xml, "//note/to/@test");
        assertEquals(2, l.size());
        assertEquals("ggg1 ggg2", l.stream().collect(joining(" ")));

        l = Xml.query(xml, "/notes/note/to[@test=\"ggg2\"]");
        assertEquals(1, l.size());
        assertEquals("Bove", l.stream().collect(joining(" ")));

        l = Xml.query(xml, "/notes/note/to");
        assertEquals(2, l.size());
        assertEquals("Tove Bove", l.stream().collect(joining(" ")));

        l = Xml.query(xml, "/notes/note[2]");
        assertEquals(1, l.size());
        assertEquals("Bove", l.stream().collect(joining(" ")));

        l = Xml.query(xml, "/notes/note/to/@test");
        assertEquals(2, l.size());
        assertEquals("ggg1 ggg2", l.stream().collect(joining(" ")));
    }

    @Test
    public void test_replace() throws Exception {
        String xml =
                "<notes><note><to test=\"ggg1\">Tove</to></note><note><to"
                        + " test=\"ggg2\">Bove</to></note></notes>";
        String actual = Xml.replace(xml, "//note/to/@test", "lol");
        String expected =
                "<notes><note><to test=\"lol\">Tove</to></note><note><to"
                        + " test=\"lol\">Bove</to></note></notes>";
        assertEquals(expected, actual);
    }
}
