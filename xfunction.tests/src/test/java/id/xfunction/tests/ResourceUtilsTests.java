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
package id.xfunction.tests;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import id.xfunction.ResourceUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ResourceUtilsTests {

    private ResourceUtils resourceUtils;

    @BeforeEach
    public void setup() {
        resourceUtils = new ResourceUtils();
    }

    @Test
    public void test_readResourceAsStream() {
        List<String> actual = resourceUtils.readResourceAsStream("testFile").collect(toList());
        assertEquals("[line 1, line 2]", actual.toString());
    }

    @Test
    public void test_readResourceAsStream_with_class() {
        List<String> actual =
                resourceUtils.readResourceAsStream(this.getClass(), "testFile2").collect(toList());
        assertEquals("[line 1, line 2]", actual.toString());
    }

    @Test
    public void test_readResource() {
        String actual = resourceUtils.readResource("testFile");
        assertEquals("line 1\nline 2", actual.toString());
    }

    @Test
    public void test_readResource_with_class() {
        String actual = resourceUtils.readResource(this.getClass(), "testFile2");
        assertEquals("line 1\nline 2", actual.toString());
    }

    @Test
    public void test_extractResource() throws IOException {
        Path out = Files.createTempFile("resource", "");
        resourceUtils.extractResource("testFile", out);
        assertEquals(13, Files.size(out));

        Files.delete(out);
        resourceUtils.extractResource("testFile", out);
        assertEquals(13, Files.size(out));
    }
}
