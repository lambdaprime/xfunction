/*
 * Copyright 2022 lambdaprime
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
package id.xfunction.tests.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import id.xfunction.XAssertException;
import id.xfunction.util.IntBitSet;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class IntBitSetTest {

    @Test
    public void test_1_and_2_words() {
        IntBitSet set = new IntBitSet(3);
        set.flip(2);
        assertEquals("100", set.toBinaryString());
        set.flip(2);
        set.flip(1);
        set.flip(0);
        assertEquals("11", set.toBinaryString());

        set = new IntBitSet(35);
        set.flip(1);
        set.flip(0);
        set.flip(8);
        assertEquals("100000011", set.toBinaryString());
        set.flip(7);
        assertEquals("110000011", set.toBinaryString());
        set.flip(7);
        set.flip(33);

        // ______________30________20________10________0
        assertEquals("1000000000000000000000000100000011", set.toBinaryString());
        set.flip(32);
        assertEquals("1100000000000000000000000100000011", set.toBinaryString());
        set.flip(8);
        assertEquals("1100000000000000000000000000000011", set.toBinaryString());

        set.flip(5);

        // ______________30________20________10________0
        assertEquals("1100000000000000000000000000100011", set.toBinaryString());

        set.flip(0, 5);
        assertEquals("1100000000000000000000000000111100", set.toBinaryString());

        set.flip(7, 7);
        assertEquals("1100000000000000000000000000111100", set.toBinaryString());

        set.flip(0, 32);
        assertEquals("1111111111111111111111111111000011", set.toBinaryString());

        set.flip(32, 32);

        // ______________30________20________10________0
        assertEquals("1111111111111111111111111111000011", set.toBinaryString());

        set.flip(31, 32);
        assertEquals("1101111111111111111111111111000011", set.toBinaryString());

        set.flip(0, 33);

        // ______________30________20________10________0
        assertEquals("1010000000000000000000000000111100", set.toBinaryString());

        set.flip(0, 33);

        // ______________30________20________10________0
        assertEquals("1101111111111111111111111111000011", set.toBinaryString());

        set.flip(0, 35);

        // ______________30________20________10________0
        assertEquals("10010000000000000000000000000111100", set.toBinaryString());
    }

    @Test
    public void test_4_words() {
        IntBitSet set = new IntBitSet(128);
        Assertions.assertThrows(XAssertException.class, () -> set.flip(128));

        set.flip(127);
        assertEquals(
                ""
                        + "10000000"
                        + "0000000000" // 110..119
                        + "0000000000" // 100..109
                        + "0000000000" // 90..99
                        + "0000000000" // 80..89
                        + "0000000000" // 70..79
                        + "0000000000" // 60..69
                        + "0000000000" // 50..59
                        + "0000000000" // 40..49
                        + "0000000000" // 30..39
                        + "0000000000" // 20..29
                        + "0000000000" // 10..19
                        + "0000000000", // 0..9
                set.toBinaryString());

        set.flip(0, 64);
        assertEquals(
                ""
                        + "10000000"
                        + "0000000000" // 110..119
                        + "0000000000" // 100..109
                        + "0000000000" // 90..99
                        + "0000000000" // 80..89
                        + "0000000000" // 70..79
                        + "0000001111" // 60..69
                        + "1111111111" // 50..59
                        + "1111111111" // 40..49
                        + "1111111111" // 30..39
                        + "1111111111" // 20..29
                        + "1111111111" // 10..19
                        + "1111111111", // 0..9
                set.toBinaryString());

        set.flip(0, 64);
        set.flip(32, 128);
        assertEquals(
                ""
                        + "1111111"
                        + "1111111111" // 110..119
                        + "1111111111" // 100..109
                        + "1111111111" // 90..99
                        + "1111111111" // 80..89
                        + "1111111111" // 70..79
                        + "1111111111" // 60..69
                        + "1111111111" // 50..59
                        + "1111111111" // 40..49
                        + "1111111100" // 30..39
                        + "0000000000" // 20..29
                        + "0000000000" // 10..19
                        + "0000000000", // 0..9
                set.toBinaryString());

        set.flip(90, 100);
        assertEquals(
                ""
                        + "1111111"
                        + "1111111111" // 110..119
                        + "1111111111" // 100..109
                        + "0000000000" // 90..99
                        + "1111111111" // 80..89
                        + "1111111111" // 70..79
                        + "1111111111" // 60..69
                        + "1111111111" // 50..59
                        + "1111111111" // 40..49
                        + "1111111100" // 30..39
                        + "0000000000" // 20..29
                        + "0000000000" // 10..19
                        + "0000000000", // 0..9
                set.toBinaryString());
    }

    @Test
    public void test_nextSetBit() {
        var set = new IntBitSet(35);
        assertEquals(-1, set.nextSetBit(0));
        assertEquals(-1, set.nextSetBit(100));

        int[] indexes = {0, 1, 7, 8, 9, 14, 15, 16, 17, 30, 31, 32, 33};
        for (var pos : indexes) {
            set.flip(pos);
        }

        var nextSetBit = 0;
        for (int i = 0; i < indexes.length; i++) {
            nextSetBit = set.nextSetBit(nextSetBit);
            assertEquals(indexes[i], nextSetBit);
            System.out.println("Set bit at " + nextSetBit);
            nextSetBit++;
        }

        var actual = new ArrayList<>();
        for (int i = set.nextSetBit(0); i >= 0; i = set.nextSetBit(i + 1)) {
            actual.add(i);
        }
        assertEquals(Arrays.toString(indexes), actual.toString());
    }
}
