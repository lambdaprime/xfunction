/*
 * Copyright 2023 lambdaprime
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

import static id.xfunction.Checksum.md5;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

public class ChecksumTests {

    @Test
    public void test_md5Sum() throws Exception {
        assertEquals(
                "F368382E27B21F0D6DB6693DEBE94A14", md5("I can see what you want").toUpperCase());
        assertEquals("2DDAFC008B12810D7148E3E6943598AC", md5("A state of trance").toUpperCase());
        assertEquals(
                "31CF05BEC43849BED31296557B064071", md5("until the sky falls down").toUpperCase());
    }

    @Test
    public void test_md5Sum_file() throws Exception {
        Path file = Files.createTempFile("gg", "");
        Files.writeString(file, "very long string".repeat(100));
        assertEquals("05ee552a62cdfeb6b21ac40f401c5eed", md5(file.toFile()));
        file.toFile().delete();
    }
}
