/*
 * Copyright 2025 lambdaprime
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

import id.xfunction.util.CircularArray;
import org.junit.jupiter.api.Test;

public class CircularArrayTest {
    @Test
    public void testAvgWithFullBuffer() {
        CircularArray array = new CircularArray(3);
        array.add(10.0);
        array.add(20.0);
        array.add(30.0);

        assertEquals(20.0, array.avg(), 0.001);
    }

    @Test
    public void testAvgWithHalfFullBuffer() {
        CircularArray array = new CircularArray(3);
        array.add(10.0);
        assertEquals(1, array.getSize());
        array.add(20.0);
        assertEquals(2, array.getSize());
        assertEquals(15.0, array.avg(), 0.001);
    }

    @Test
    public void testAvgWithEmptyBuffer() {
        CircularArray array = new CircularArray(3);

        assertEquals(0.0, array.avg(), 0.001);
    }

    @Test
    public void testAddMultipleValues() {
        CircularArray array = new CircularArray(2);
        array.add(10.0);
        array.add(20.0);
        array.add(30.0);
        array.add(40.0);

        assertEquals(35.0, array.avg(), 0.001); // only first value is in the buffer
    }

    @Test
    public void testAddLargeNumberValues() {
        CircularArray array = new CircularArray(10_000);
        for (int i = 0; i < 1_000; i++) {
            array.add(i * 1.0);
        }
        assertEquals(499.5, array.avg(), 0.001); // average of first and last value
    }
}
