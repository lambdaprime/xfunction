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

import id.xfunction.nio.file.XFiles;
import id.xfunction.nio.file.XPaths;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

public class XPathsTests {

    @Test
    public void test_copyRecursively() throws IOException {
        assertEquals("[, null]", Arrays.toString(XPaths.splitFileName("")));
        assertEquals("[.file, null]", Arrays.toString(XPaths.splitFileName(".file")));
        assertEquals("[file, ext]", Arrays.toString(XPaths.splitFileName("file.ext")));
        assertEquals("[file.ext, jpg]", Arrays.toString(XPaths.splitFileName("file.ext.jpg")));
    }

    @Test
    public void test_append() throws IOException {
        Path tmpFolder = XFiles.TEMP_FOLDER.get();
        assertEquals(
                tmpFolder.resolve("a-cropped.png"),
                XPaths.append(tmpFolder.resolve("a.png"), "-cropped"));
        assertEquals(
                tmpFolder.resolve("a-cropped"), XPaths.append(tmpFolder.resolve("a"), "-cropped"));
    }
}
