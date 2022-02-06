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

import id.xfunction.io.XOutputStream;

public class XOutputStreamTest {

    @Test
    public void test() throws Exception {
        try (XOutputStream out = new XOutputStream()) {
            out.write(-16);
            out.write(0);
            out.write(126);
            assertEquals("f0, 00, 7e", out.asHexString());
        }
    }
    
    @Test
    public void test2() throws Exception {
        try (XOutputStream out = new XOutputStream()) {
            out.write("abc".getBytes());
            assertEquals("61, 62, 63", out.asHexString());
            assertEquals("[97, 98, 99]", out.asList().toString());
        }
    }
    
    @Test
    public void test3() throws Exception {
        try (XOutputStream out = new XOutputStream()) {
            out.write("hello".getBytes());
            assertEquals("68, 65, 6c, 6c, 6f", out.asHexString());
            assertEquals("[104, 101, 108, 108, 111]", out.asList().toString());
        }
    }
}
