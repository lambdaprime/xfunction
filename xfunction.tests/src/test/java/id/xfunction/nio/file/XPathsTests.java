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

package id.xfunction.nio.file;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Paths;
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
        assertEquals("/tmp/a-cropped.png", XPaths.append(Paths.get("/tmp/a.png"), "-cropped").toString());
        assertEquals("/tmp/a-cropped", XPaths.append(Paths.get("/tmp/a"), "-cropped").toString());
    }
    
}
