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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import id.xfunction.io.ByteBufferUtils;
import java.nio.ByteBuffer;
import org.junit.jupiter.api.Test;

public class ByteBufferUtilsTest {

    @Test
    public void test_shiftToHead() throws Exception {
        ByteBuffer buf = ByteBuffer.wrap(new byte[] {0xa, 0xa, 0xa, 0xa, 0xb, 0xb, 0xb});
        ByteBufferUtils utils = new ByteBufferUtils();
        utils.copyToHead(buf, 4, 7);
        assertArrayEquals(new byte[] {0xb, 0xb, 0xb, 0xa, 0xb, 0xb, 0xb}, buf.array());

        var expectedPos = buf.position();
        assertEquals("0a 0b 0b 0b", utils.asString(buf));
        assertEquals(expectedPos, buf.position());
    }
}
