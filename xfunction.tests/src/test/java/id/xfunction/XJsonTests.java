/*
 * Copyright 2020 lambdaprime
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
package id.xfunction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class XJsonTests {

    class Obj {
        @Override
        public String toString() {
            return XJson.asString(
                "k1", "v1",
                "k2", "v2");
        }
    }

    @Test
    public void test_asJson_list() {
        assertEquals(XUtils.readResource(getClass(), "json-list"), XJson.asString(
            "k1", 123,
            "k2", "ggg",
            "k3", List.of("sg", "dfg", "dsfg")));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void test_asJson_map() {
        Map m = new HashMap<>();
        m.put("23", Integer.valueOf(34));
        m.put("bbs", Double.valueOf(3.23));
        m.put("abs", "bbb");
        assertEquals(XUtils.readResource(getClass(), "json-map"), XJson.asString(
            "k1", 123,
            "k2", "ggg",
            "k3", m));
    }
    
    @Test
    public void test_asJson_nested() {
        assertEquals(XUtils.readResource(getClass(), "json-nested"), XJson.asString(
            "obj1", new Obj(),
            "obj2", new Obj(),
            "k3", List.of("sg", "dfg", "dsfg")));
    }

    @Test
    public void test_empty() {
        assertEquals("{ \"k1\": \"\" }", XJson.asString(
            "k1", ""));
    }
}
