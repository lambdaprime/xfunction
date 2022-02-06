/*
 * Copyright 2021 lambdaprime
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

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import id.xfunction.nio.file.XFiles;

public class XFilesTests {

    @Test
    public void test_copyRecursively() throws IOException {
        Path tmpDir = Files.createTempDirectory("test");
        XFiles.copyRecursively(Paths.get("src/test/resources/a"), tmpDir);
        assertTrue(Files.list(tmpDir).count() > 0);
        XFiles.deleteRecursively(tmpDir);
        Assertions.assertEquals(false, tmpDir.toFile().exists());
    }
}
