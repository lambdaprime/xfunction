/*
 * Copyright 2022 lambdaprime
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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import id.xfunction.XAssertException;
import id.xfunction.util.LongNumberSequence;

public class LongNumberSequenceTest {

    @Test
    public void test_empty() {
        LongNumberSequence seq = new LongNumberSequence(3);
        assertEquals(true, seq.hasMissing());
    }

    @Test
    public void test_negative() {
        LongNumberSequence seq = new LongNumberSequence(3);
        Assertions.assertThrows(XAssertException.class, () -> seq.add(-1));
    }

    @Test
    public void test_seq_starts_from_1() {
        LongNumberSequence seq = new LongNumberSequence(3);
        seq.add(1);
        assertEquals(false, seq.hasMissing());
    }

    @Test
    public void test_seq_starts_not_from_1() {
        LongNumberSequence seq = new LongNumberSequence(3);
        seq.add(5);
        assertEquals(true, seq.hasMissing());
        assertEquals("[3, 4]", seq.getMissing().toString());
    }

    @Test
    public void test_add() {
        LongNumberSequence seq = new LongNumberSequence(3);
        seq.add(5);
        assertEquals(true, seq.hasMissing());
        assertEquals("[3, 4]", seq.getMissing().toString());
        seq.add(6);
        assertEquals(true, seq.hasMissing());
        assertEquals("[4]", seq.getMissing().toString());
        seq.add(7);
        assertEquals(false, seq.hasMissing());
        assertEquals("[]", seq.getMissing().toString());
        seq.add(8);
        assertEquals(false, seq.hasMissing());
        assertEquals("[]", seq.getMissing().toString());
        seq.add(9);
        assertEquals(false, seq.hasMissing());
        assertEquals("[]", seq.getMissing().toString());
        seq.add(10);
        assertEquals(false, seq.hasMissing());
        assertEquals("[]", seq.getMissing().toString());
        seq.add(17);
        assertEquals(true, seq.hasMissing());
        assertEquals("[15, 16]", seq.getMissing().toString());
        seq.add(17);
        assertEquals(true, seq.hasMissing());
        assertEquals("[15, 16]", seq.getMissing().toString());
    }

    @Test
    public void test1() {
        LongNumberSequence seq = new LongNumberSequence(10);
        assertEquals("[1, 2, 3, 4, 5, 6, 7, 8, 9, 10]", seq.getMissing().toString());
        assertEquals("[1, 2, 3, 4, 5, 6, 7, 8]", seq.getMissing(1, 8).toString());
        seq.add(5);
        seq.add(4);
        assertEquals("[1, 2, 3, 6, 7, 8]", seq.getMissing(1, 8).toString());
        seq.add(13);
        assertEquals("[6, 7, 8, 9, 10, 11, 12]", seq.getMissing().toString());
        assertEquals("[6, 7, 8, 9, 10, 11, 12]", seq.getMissing(1, 13).toString());
        assertEquals("[]", seq.getMissing(1, 3).toString());
    }
}
