/*
 * Copyright 2019 lambdaprime
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
package id.xfunction.tests.text;

import static org.junit.jupiter.api.Assertions.assertEquals;

import id.xfunction.text.Substitutor;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class SubstitutorTests {

    @Test
    public void test_substitute() {
        Substitutor substitutor = new Substitutor();
        assertEquals(
                "[aaaabbb]",
                substitutor.substitute(List.of("aaa{A}bbb"), Map.of("{A}", "a")).toString());
        assertEquals(
                "[aaaabbb]",
                substitutor
                        .substitute(
                                List.of("a{A}a{A}b{B}b"),
                                Map.of(
                                        "{A}", "a",
                                        "{B}", "b"))
                        .toString());
        assertEquals(
                "[aaaa, bbb]",
                substitutor
                        .substitute(
                                List.of("a{A}a{A}", "b{B}b"),
                                Map.of(
                                        "{A}", "a",
                                        "{B}", "b"))
                        .toString());
    }

    @Test
    public void test_substitute_file() throws IOException {
        Path path = Files.createTempFile("tmp", "");
        Files.writeString(path, "a{A}a{A}" + System.lineSeparator() + "b{B}b");
        Substitutor substitutor = new Substitutor();
        substitutor.substitute(
                path,
                Map.of(
                        "{A}", "a",
                        "{B}", "b"));
        List<String> lines = Files.readAllLines(path);
        assertEquals("[aaaa, bbb]", lines.toString());
    }
}
