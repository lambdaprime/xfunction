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
package id.xfunction.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import id.xfunction.ResourceUtils;
import id.xfunction.XJson;

public class XJsonTests {

    private ResourceUtils resourceUtils = new ResourceUtils();

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
        assertEquals(resourceUtils.readResource(getClass(), "json-list"), XJson.asString(
            "k1", 123,
            "k2", "ggg",
            "k3", List.of("sg", "dfg", "dsfg")));
    }

    @Test
    public void test_asJson_null() {
        List<String> s = new ArrayList<>();
        s.add("sasa");
        s.add(null);
        s.add("hh");
        assertEquals(resourceUtils.readResource(getClass(), "json-null"), XJson.asString(
            "k1", null,
            "k2", "ggg",
            "k3", s));
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void test_asJson_map() {
        Map m = new HashMap<>();
        m.put("23", Integer.valueOf(34));
        m.put("bbs", Double.valueOf(3.23));
        m.put("abs", "bbb");
        assertEquals(resourceUtils.readResource(getClass(), "json-map"), XJson.asString(
            "k1", 123,
            "k2", "ggg",
            "k3", m));
    }
    
    @Test
    public void test_asJson_nested() {
        assertEquals(resourceUtils.readResource(getClass(), "json-nested"), XJson.asString(
            "obj1", new Obj(),
            "obj2", new Obj(),
            "k3", List.of("sg", "dfg", "dsfg")));
    }

    @Test
    public void test_asJson_collection_with_null() {
        assertEquals(resourceUtils.readResource(getClass(), "json-collection-null"), XJson.asString(
            "obj1", new Obj(),
            "obj2", new Obj(),
            "k3", Arrays.asList(new String[] {null})));
    }

    @Test
    public void test_empty() {
        assertEquals("{ \"k1\": \"\" }", XJson.asString(
            "k1", ""));
    }
    
    @Test
    public void test_merge() {
        assertEquals(resourceUtils.readResource(getClass(), "json-merge"), XJson.merge(
            XJson.asString(
                "k1", "v1",
                "k2", "v2"),
            XJson.asString(
                "k3", "v3",
                "k4", "v4"),
            XJson.asString(
                "k5", "v5")));
    }
    
    @Test
    public void test_list_of_jsons() {
        assertEquals(resourceUtils.readResource(getClass(), "json-list-jsons"), XJson.asString(
            "k3", List.of(XJson.asString(
                "k1", "v1",
                "k2", "v2"),
            XJson.asString(
                "k3", "v3",
                "k4", "v4"),
            XJson.asString(
                "k5", "v5"))));
    }
    
    @Test
    public void test_asJson_array() {
        assertEquals(resourceUtils.readResource(getClass(), "json-array"), XJson.asString(
            "obj1", new Obj(),
            "obj2", new Obj(),
            "k3", new int[] {1, 2, 3}));
    }
    
    @Test
    public void test_asJson_array_objects() {
        assertEquals(resourceUtils.readResource(getClass(), "json-array"), XJson.asString(
            "obj1", new Obj(),
            "obj2", new Obj(),
            "k3", new String[] {"1", "2", "3"}));
    }
    
    @Test
    public void test_asJson_rounding() {
        String rounded = resourceUtils.readResource(getClass(), "json-list-decimals-rounded");
        Object[] testData = {
            "k1", 123.12321345,
            "k2", 0.314,
            "k3", 0.0000,
            "k4", List.of(0.12345, 1.54321, 0.0000, 1234.657899999),
            "k5", 123456789
        };
        String before = XJson.asString(testData);

        XJson.setLimitDecimalPlaces(2);
        assertEquals(rounded, XJson.asString(testData));

        XJson.setLimitDecimalPlaces(-1);
        assertEquals(before, XJson.asString(testData));
    }

    @Test
    public void test_setNegativeZero() {
        String zeroes = resourceUtils.readResource(getClass(), "json-list-zeroes");
        String zeroesRounded = resourceUtils.readResource(getClass(), "json-list-zeroes-rounded");
        Object[] testData = {
            "k1", -0.0000,
            "k2", 0.0000,
            "k3", 0.0001,
            "k4", -0.0001,
            "k5", -0,
            "k6", 0,
            "k7", -0.00003,
            "k8", List.of(-0.0000, 0.0000, 0.0001, -0.0001, -0, 0, -0.00003),
            "k9", new double[] {-0.0000, 0.0000, 0.0001, -0.0001, -0.00003,
                /* since it is double array next two ints will be doubles */ -0, 0}
        };
        XJson.setNegativeZero(false);
        assertEquals(zeroes, XJson.asString(testData));
        XJson.setLimitDecimalPlaces(2);
        assertEquals(zeroesRounded, XJson.asString(testData));
        XJson.setLimitDecimalPlaces(-1);
        XJson.setNegativeZero(true);
    }
}
