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

import id.xfunction.ResourceUtils;
import id.xfunction.text.Substitutor;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class SubstitutorTests {
    private static ResourceUtils resourceUtils = new ResourceUtils();

    public static Stream<Arguments> data_substitutePerLine() {
        return Stream.of(
                Arguments.of("[aaaabbb]", List.of("aaa{A}bbb"), Map.of("{A}", "a")),
                Arguments.of(
                        "[aaaabbb]",
                        List.of("a{A}a{A}b{B}b"),
                        Map.of(
                                "{A}", "a",
                                "{B}", "b")),
                Arguments.of(
                        "[aaaa, bbb]",
                        List.of("a{A}a{A}", "b{B}b"),
                        Map.of(
                                "{A}", "a",
                                "{B}", "b")));
    }

    @ParameterizedTest
    @MethodSource("data_substitutePerLine")
    public void test_substitutePerLine(
            String expected, List<String> lines, Map<String, String> mapping) {
        Substitutor substitutor = new Substitutor();
        assertEquals(expected, substitutor.substitutePerLine(lines, mapping).toString());
    }

    public static Stream<Arguments> data_substitute() {
        return Stream.of(
                Arguments.of("aaaabbb", "aaa{A}bbb", Map.of("{A}", "a"), false),
                Arguments.of(
                        "aaaabbb",
                        "a{A}a{A}b{B}b",
                        Map.of(
                                "{A}", "a",
                                "{B}", "b"),
                        false),
                Arguments.of(
                        "aaaa, bbb",
                        "a{A}a{A}" + System.lineSeparator() + "b{B}b",
                        Map.of("{A}", "a", "{B}", "b", System.lineSeparator(), ", "),
                        false),
                Arguments.of(
                        resourceUtils.readResource(
                                SubstitutorTests.class, "test_substitute1_expected"),
                        resourceUtils.readResource(SubstitutorTests.class, "test_substitute1"),
                        Map.of("(?s)(?m)exports \\S* to.*;", ""),
                        true));
    }

    @ParameterizedTest
    @MethodSource("data_substitute")
    public void test_substitute(
            String expected, String lines, Map<String, String> mapping, boolean hasRegexp) {
        Substitutor substitutor = new Substitutor();
        if (hasRegexp) substitutor.withRegexpSupport();
        assertEquals(expected, substitutor.substitute(lines, mapping).toString());
    }

    @Test
    public void test_substitutePerLine_file() throws IOException {
        Path path = Files.createTempDirectory("tmp");
        Path file1 = path.resolve("test");
        String text = "a{A}a{A}" + System.lineSeparator() + "b{B}b";
        Files.writeString(file1, text);
        var dir1 = path.resolve("dir1");
        Path file2 = Files.createDirectory(dir1).resolve("test0");
        Files.writeString(file2, text);
        Path file3 = dir1.resolve("test1");
        Files.write(file3, List.of("fff", "hhhh"));
        Substitutor substitutor = new Substitutor();
        var updatedFiles =
                substitutor.substitutePerLine(
                        path,
                        Map.of(
                                "{A}", "a",
                                "{B}", "b"));
        assertEquals(List.of(file1, file2).toString(), updatedFiles.toString());
        List<String> lines = Files.readAllLines(file1);
        String expected = "[aaaa, bbb]";
        assertEquals(expected, lines.toString());
        lines = Files.readAllLines(file2);
        assertEquals(expected, lines.toString());
    }
}
