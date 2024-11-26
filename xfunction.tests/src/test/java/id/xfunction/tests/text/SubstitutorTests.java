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
import id.xfunction.nio.file.XFiles;
import id.xfunction.text.Substitutor;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
        XFiles.copyRecursively(Paths.get("samples"), path);
        Path file1 = path.resolve("f1");
        Path file2 = path.resolve("b/g/test");
        Substitutor substitutor = new Substitutor();
        var updatedFiles =
                substitutor.substitutePerLine(
                        path,
                        Map.of(
                                "{A}", "a",
                                "{B}", "b"));
        assertEquals(Set.of(file1, file2), Set.copyOf(updatedFiles));
        assertEquals("[hello, aaaa]", Files.readAllLines(file1).toString());
        assertEquals("[test, aaaabbb]", Files.readAllLines(file2).toString());
    }

    @Test
    public void test_substitute_file() throws IOException {
        Path path = Files.createTempDirectory("tmp");
        XFiles.copyRecursively(Paths.get("samples"), path);
        Path file1 = path.resolve("f1");
        Path file2 = path.resolve("b/g/test");
        Substitutor substitutor = new Substitutor();
        var updatedFiles =
                substitutor.substitute(
                        path,
                        Map.of(
                                "{A}", "a\n",
                                "test\na", "b"));
        assertEquals(Set.of(file1, file2), Set.copyOf(updatedFiles));
        assertEquals("[hello, aa, aa, ]", Files.readAllLines(file1).toString());
        assertEquals("[ba, aa, b{B}b]", Files.readAllLines(file2).toString());
    }
}
