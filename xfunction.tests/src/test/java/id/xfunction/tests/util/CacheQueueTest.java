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

import id.xfunction.util.CacheQueue;
import java.util.List;
import org.junit.jupiter.api.Test;

public class CacheQueueTest {

    @Test
    public void test_happy() {
        var queue = new CacheQueue<Integer>(3);
        assertEquals("[]", queue.toString());
        queue.add(1);
        queue.add(2);
        queue.add(3);
        assertEquals("[1, 2, 3]", queue.toString());
        assertEquals(false, queue.add(3));
        assertEquals("[1, 2, 3]", queue.toString());
        assertEquals(false, queue.add(1));
        assertEquals("[1, 2, 3]", queue.toString());
        assertEquals(true, queue.add(-1));
        assertEquals("[2, 3, -1]", queue.toString());
        assertEquals(true, queue.add(1));
        assertEquals("[3, -1, 1]", queue.toString());
        assertEquals(false, queue.addAll(List.of(1, 3)));
        assertEquals(true, queue.addAll(List.of(1, 3, 5, 6, 7)));
        assertEquals("[5, 6, 7]", queue.toString());
        assertEquals(false, queue.remove(2));
        assertEquals(true, queue.remove(6));
        assertEquals("[5, 7]", queue.toString());
        assertEquals(false, queue.isEmpty());
        assertEquals(true, queue.removeAll(List.of(1, 3, 7, 5)));
        assertEquals(true, queue.isEmpty());
    }
}
