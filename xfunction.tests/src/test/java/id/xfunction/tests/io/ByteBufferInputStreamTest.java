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
package id.xfunction.tests.io;

import static org.junit.jupiter.api.Assertions.assertEquals;

import id.xfunction.io.ByteBufferInputStream;
import java.nio.ByteBuffer;
import org.junit.jupiter.api.Test;

public class ByteBufferInputStreamTest {

    @Test
    public void test_read() throws Exception {
        ByteBuffer buf = ByteBuffer.wrap(new byte[] {(byte) 0xff});
        try (ByteBufferInputStream is = new ByteBufferInputStream(buf)) {
            assertEquals(255, is.read());
            assertEquals(-1, is.read());
        }
    }
}
