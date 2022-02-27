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
package id.xfunction.tests.io;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import id.xfunction.io.XInputStream;

public class XInputStreamTest {

    @Test
    public void test() throws Exception {
        try (XInputStream out = new XInputStream("68,   65 6c\n, 6c\n6f")) {
            byte[] buf = new byte[5];
            out.read(buf);
            assertEquals("hello", new String(buf));
            assertEquals(-1, out.read());
        }
    }

}
