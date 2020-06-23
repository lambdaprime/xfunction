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
import static java.util.stream.Collectors.toMap;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringJoiner;
import java.util.stream.Stream;

/**
 * Set of functions to work with JSON
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
            if (vstr.isEmpty() || vstr.charAt(0) != '{')
                vstr = XUtils.quote(vstr);
            if (v instanceof Map) {
                vstr = asString((Map<?, ?>)v);
            } else if (v instanceof Collection) {
                Collection<?> c = (Collection<?>) v;
                vstr = c.stream()
                    .map(Object::toString)
                    .map(XUtils::quote)
                    .collect(joining(", "));
                vstr = "[" + vstr + "]";
            }
            buf.add("\"" + k + "\": " + vstr);
        }
        return "{ " + buf.toString() + " }";
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
}
