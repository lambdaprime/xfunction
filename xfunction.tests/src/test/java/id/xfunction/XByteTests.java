/*
 * Copyright 2019 lambdaprime
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
package id.xfunction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class XByteTests {

    @Test
    public void test_sample() throws Exception {
        assertEquals("68656c6c6f20776f726c64", XByte.toHex("hello world".getBytes()));
        assertEquals("68 65 6c 6c 6f 20 77 6f 72 6c 64", XByte.toHexPairs("hello world".getBytes()));
        assertEquals("00 00 00 7b", XByte.toHexPairs(123));
    }

}
