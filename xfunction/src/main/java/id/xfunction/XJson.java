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

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Stream;

/**
 * Set of functions to work with JSON format
 */
public class XJson {
    
    /**
     * <p>Build a JSON from a given pair of objects (k1, v1, k2, v2, ...).
     * String for each object is obtained by calling toString on the object itself.</p>
     * 
     * <p>Example:</p>
     * 
     * <pre>{@code
     * XJson.asString(
     *     "k1", 123,
     *     "k2", "ggg",
     *     "k3", List.of("sg", "dfg", "dsfg")));
     * }</pre>
     * 
     * <p>Produces a string like: { "k1": "123", "k2": "ggg", "k3": ["sg", "dfg", "dsfg"] }</p>
     * 
     */
    public static String asString(Object...pairs) {
        XAsserts.assertTrue(pairs.length % 2 == 0, "Key-value missmatch");
        StringJoiner buf = new StringJoiner(", ");
        for (int i = 0; i < pairs.length; i += 2) {
            Object k = pairs[i];
            Object v = pairs[i + 1];
            String vstr = v.toString();
            if (vstr == null)
                vstr = "";
            vstr = quote(vstr);
            if (v instanceof Map) {
                vstr = asString((Map<?, ?>)v);
            } else if (v instanceof Collection) {
                Collection<?> c = (Collection<?>) v;
                vstr = c.stream()
                    .map(Objects::toString)
                    .map(XJson::quote)
                    .collect(joining(", "));
                vstr = "[" + vstr + "]";
            }
            buf.add("\"" + k + "\": " + vstr);
        }
        return "{ " + buf.toString() + " }";
    }
    
    private static String quote(String value) {
        if (value.isEmpty() || value.charAt(0) != '{')
            return XUtils.quote(value);
        return value;
    }

    /**
     * <p>Build a JSON from a given pair of objects (k1, v1, k2, v2, ...).
     * String for each object is obtained by calling toString on the object itself.</p>
     * 
     * <p>All pairs will be processed in lexicographical order of their keys.</p>
     */
    public static <K, V> String asString(Map<K, V> m) {
        return asString(m.entrySet().stream()
            .collect(toMap(e -> e.getKey().toString(), Entry::getValue))
            .entrySet().stream()
            .sorted(Entry.comparingByKey())
            .flatMap(e -> Stream.<Object>of(e.getKey(), e.getValue()))
            .toArray());
    }
    
    /**
     * <p>Merges multiple JSONs into one.</p>
     * <p>Useful when you override toString on a subclass which returns JSON. In that case
     * you can use this method to merge it with JSON produced from its super class.</p>
     * <p>Example:</p>
     * 
     * <pre>{@code
     * return XJson.merge(
     *     super.toString(),
     *     XJson.asString(
     *         "k1", "v1",
     *         "k2", "v2"));
     * }</pre>
     */
    public static String merge(String...jsons) {
        String json = "";
        if (jsons.length == 0) return json;
        List<String> list = Arrays.stream(jsons)
            .map(String::trim)
            .collect(toList());
        if (list.size() == 1) return list.get(0);
        json = list.get(0);
        for (int i = 1; i < jsons.length; ++i) {
            json = json.substring(0, json.length() - 1);
            json = json.trim();
            json += ",";
            json += jsons[i].substring(1);
        }
        return json;
    }
}
