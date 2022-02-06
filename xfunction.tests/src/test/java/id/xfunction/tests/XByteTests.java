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
package id.xfunction.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import id.xfunction.XByte;

public class XByteTests {

    @Test
    public void test_sample() throws Exception {
        assertEquals("68656c6c6f20776f726c64", XByte.toHex("hello world".getBytes()));
        assertEquals("68 65 6c 6c 6f 20 77 6f 72 6c 64", XByte.toHexPairs("hello world".getBytes()));
        assertEquals("00 00 00 7b", XByte.toHexPairs(123));
        
        assertEquals(0xc0, Byte.toUnsignedInt(XByte.reverseBits((byte) 0x03)));
        assertEquals(0b01010000, Byte.toUnsignedInt(XByte.reverseBits((byte) 0b00001010)));

        assertEquals(0xc0, Integer.toUnsignedLong(XByte.reverseBytes(0x03)));
        assertEquals(0b11000000_01010000, Integer.toUnsignedLong(XByte.reverseBytes(0b00000011_00001010)));
        assertEquals(0b01110000_11000000_01010000, Integer.toUnsignedLong(XByte.reverseBytes(0b00001110_00000011_00001010)));
    }

}
