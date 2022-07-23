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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import id.xfunction.lang.XThread;
import id.xfunction.nio.file.XFiles;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class XFilesTests {

    @Test
    public void test_copyRecursively() throws IOException {
        Path tmpDir = Files.createTempDirectory("test");
        XFiles.copyRecursively(Paths.get("src/test/resources/a"), tmpDir);
        assertTrue(Files.list(tmpDir).count() > 0);
        XFiles.deleteRecursively(tmpDir);
        Assertions.assertEquals(false, tmpDir.toFile().exists());
    }

    @Test
    public void test_watchForStringInFile() throws Exception {
        Path tmpFile = Files.createTempFile("test", "");
        var future = XFiles.watchForLineInFile(tmpFile, s -> s.contains("hello"));
        assertEquals(false, future.isDone());
        Files.write(tmpFile, "asdsa\nd".getBytes(), StandardOpenOption.APPEND);
        XThread.sleep(200);
        assertEquals(false, future.isDone());

        Files.write(tmpFile, "hell".getBytes(), StandardOpenOption.APPEND);
        XThread.sleep(200);
        assertEquals(false, future.isDone());

        Files.write(tmpFile, "o\n".getBytes(), StandardOpenOption.APPEND);
        XThread.sleep(200);
        assertEquals("dhello", future.get());

        future = XFiles.watchForLineInFile(tmpFile, s -> s.contains("hel"));
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
}
