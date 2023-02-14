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
package id.xfunction.tests.util.stream;

import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import id.xfunction.util.stream.XStream;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class XStreamTests {

    @Test
    public void test_infiniteRandomStream() {
        Set<String> s1 = XStream.infiniteRandomStream(10).limit(5).collect(toSet());
        Set<String> s2 = XStream.infiniteRandomStream(10).limit(5).collect(toSet());
        assertNotEquals(s1, s2);
    }

    @Test
    public void test_infiniteRandomStream_limit_1() {
        Set<String> s1 = XStream.infiniteRandomStream(10).limit(1).collect(toSet());
        assertEquals(1, s1.size());
    }
}
