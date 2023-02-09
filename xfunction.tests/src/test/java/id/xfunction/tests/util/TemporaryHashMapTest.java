/*
 * Copyright 2023 lambdaprime
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

import id.xfunction.lang.XThread;
import id.xfunction.util.TemporaryHashMap;
import java.time.Duration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TemporaryHashMapTest {

    @Test
    public void test() {
        // instant deletion test
        var map = new TemporaryHashMap<String, String>(Duration.ofMillis(0));
        map.put("k1", "val1");
        map.put("k2", "val2");
        Assertions.assertEquals(0, map.size());

        // test non expired items
        map = new TemporaryHashMap<String, String>(Duration.ofHours(1));
        map.put("k1", "val1");
        map.put("k2", "val2");
        Assertions.assertEquals(2, map.size());
        Assertions.assertEquals("{k1=val1, k2=val2}", map.toString());
        Assertions.assertEquals("[k1, k2]", map.keySet().toString());
        Assertions.assertEquals("[k1=val1, k2=val2]", map.entrySet().toString());
        Assertions.assertEquals("[val1, val2]", map.values().toString());
        Assertions.assertEquals("val2", map.get("k2"));
        Assertions.assertEquals(true, map.containsKey("k2"));
        Assertions.assertEquals(false, map.containsValue("k2"));
        Assertions.assertEquals(true, map.containsValue("val2"));

        // expiration scale test
        map = new TemporaryHashMap<String, String>(Duration.ofMillis(2));
        for (int i = 0; i < 1000; i++) {
            map.put("" + i, "val" + i);
        }
        XThread.sleep(2);
        map.put("k", "val");
        Assertions.assertEquals(1, map.size());
        Assertions.assertEquals("{k=val}", map.toString());
        Assertions.assertEquals("val", map.get("k"));
        Assertions.assertEquals(null, map.get("k2"));
        XThread.sleep(2);
        map.cleanupExpired();
        Assertions.assertEquals(true, map.isEmpty());

        // test adding same item does not change size
        map = new TemporaryHashMap<String, String>(Duration.ofMillis(100));
        for (int i = 0; i < 1000; i++) {
            map.put("k", "val" + i);
        }
        map.put("k", "val");
        Assertions.assertEquals(1, map.size());
        Assertions.assertEquals("{k=val}", map.toString());

        // time controllable tests
        var currentTimeMillis = new long[1];
        map =
                new TemporaryHashMap<String, String>(Duration.ofSeconds(1)) {
                    @Override
                    protected long currentTimeMillis() {
                        return currentTimeMillis[0];
                    }
                };
        for (int i = 0; i < 1000; i++) {
            currentTimeMillis[0] = i;
            map.put("" + i, "val" + i);
        }
        Assertions.assertEquals(1000, map.size());
        Assertions.assertEquals(true, map.containsKey("1"));

        // testing that adding existing does not increase time
        map.put("1", "val");
        Assertions.assertEquals(1000, map.size());
        currentTimeMillis[0] = 1500;
        map.put("1", "val");
        Assertions.assertEquals(false, map.containsKey("1"));
        Assertions.assertEquals(500, map.size());
        Assertions.assertEquals(true, map.containsKey("500"));
        currentTimeMillis[0] = 1501;
        map.put("600", "val");
        Assertions.assertEquals(false, map.containsKey("500"));
        Assertions.assertEquals(499, map.size());
    }
}
