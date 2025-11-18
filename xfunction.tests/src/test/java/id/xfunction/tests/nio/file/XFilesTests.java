/*
 * Copyright 2021 lambdaprime
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
package id.xfunction.tests.nio.file;

import static java.util.stream.Collectors.joining;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import id.xfunction.lang.XThread;
import id.xfunction.nio.file.XFiles;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class XFilesTests {
    private static final String NL = System.lineSeparator();

    @Test
    public void test_copyRecursively() throws IOException {
        Path tmpDir = XFiles.TEMP_FOLDER.get().resolve("test" + System.currentTimeMillis());
        XFiles.copyRecursively(Paths.get("src/test/resources/a"), tmpDir);
        Stream<Path> stream = Files.list(tmpDir);
        assertTrue(stream.count() > 0);
        // even though count is terminal operation on Windows 10/Java 17 this test fails
        // because tmpDir would still be present after deleteRecursively
        // It is present because stream is not closed and as the result tmpDir still open
        // therefore we close it explicitly
        stream.close();
        System.out.println(tmpDir);
        XFiles.deleteRecursively(tmpDir);
        Assertions.assertEquals(false, tmpDir.toFile().exists());
    }

    @Test
    public void test_watchForStringInFile() throws Exception {
        Path tmpFile = Files.createTempFile("test", "");
        var future =
                XFiles.watchForLineInFile(tmpFile, s -> s.contains("hello"), Duration.ofMillis(3));
        assertEquals(false, future.isDone());
        Files.write(tmpFile, ("asdsa" + NL + "d").getBytes(), StandardOpenOption.APPEND);
        XThread.sleep(200);
        assertEquals(false, future.isDone());

        Files.write(tmpFile, "hell".getBytes(), StandardOpenOption.APPEND);
        XThread.sleep(200);
        assertEquals(false, future.isDone());

        Files.write(tmpFile, ("o" + NL).getBytes(), StandardOpenOption.APPEND);
        XThread.sleep(200);
        assertEquals("dhello", future.get());

        future = XFiles.watchForLineInFile(tmpFile, s -> s.contains("hel"), Duration.ofMillis(13));
        assertEquals(false, future.isDone());

        future.cancel(false);
    }

    @Test
    public void test_containsAll() throws Exception {
        var samples = Paths.get("samples");
        var tmpDir = Files.createTempDirectory("test");
        assertEquals(false, XFiles.containsAll(samples, tmpDir));
        assertEquals(false, XFiles.containsAllRecursively(samples, tmpDir));

        Files.copy(samples.resolve("f1"), tmpDir.resolve("f1"));
        assertEquals(true, XFiles.containsAll(samples, tmpDir));
        assertEquals(false, XFiles.containsAllRecursively(samples, tmpDir));

        Files.delete(tmpDir.resolve("f1"));
        XFiles.copyRecursively(samples, tmpDir);
        assertEquals(true, XFiles.containsAllRecursively(samples, tmpDir));
    }

    public static Stream<Arguments> findTestCases() {
        return Stream.of(
                Arguments.of("a", "samples/a/12"),
                Arguments.of("a/*", "samples/a/12"),
                Arguments.of(
                        "a/**",
                        """
                        samples/a/12
                        samples/a/b/1
                        samples/a/b/123
                        samples/a/b/2
                        samples/a/b/c/1
                        samples/a/b/c/2
                        samples/a/b/d/2
                        samples/a/b/d/3\
                        """),
                Arguments.of("a/*2", "samples/a/12"),
                Arguments.of(
                        "a/**/2",
                        """
                        samples/a/b/2
                        samples/a/b/c/2
                        samples/a/b/d/2\
                        """),
                Arguments.of(
                        "a/**/1*3",
                        """
                        samples/a/b/123\
                        """),
                Arguments.of(
                        "*",
                        """
                        samples/f1\
                        """),
                Arguments.of("a/12", "samples/a/12"),
                Arguments.of(
                        "*/*",
                        """
                        samples/a/12
                        samples/b/f\
                        """),
                Arguments.of("a/*/c", ""),
                Arguments.of("a/**/c", ""),
                Arguments.of(
                        "a/b/*/2",
                        """
                        samples/a/b/c/2
                        samples/a/b/d/2\
                        """),
                Arguments.of(
                        "a/**/1*",
                        """
                        samples/a/b/1
                        samples/a/b/123
                        samples/a/b/c/1\
                        """));
    }

    @ParameterizedTest
    @MethodSource("findTestCases")
    public void test_find(String glob, String expected) throws Exception {
        assertEquals(
                expected,
                XFiles.findFiles(Paths.get("samples").resolve(glob).toString())
                        .map(Object::toString)
                        .sorted()
                        .collect(joining("\n")));
    }
}
