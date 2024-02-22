/*
 * Copyright 2024 lambdaprime
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
package id.xfunction.util;

import id.xfunction.XJson;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Map where each key may have multiple values assigned to it. All pairs are insertion-ordered.
 *
 * <p>Factory methods support multiple entries with same values, example:
 *
 * <pre>{@code
 * ImmutableMultiMap.of(1, "a", 2, "b", 1, "c")
 * }</pre>
 *
 * @author lambdaprime intid@protonmail.com
 */
public class ImmutableMultiMap<K, V> implements Iterable<Entry<K, List<V>>> {

    private static final ImmutableMultiMap<?, ?> EMPTY = new ImmutableMultiMap();

    private final Map<K, List<V>> map;

    public static <K1, V1> ImmutableMultiMap<K1, V1> of() {
        return (ImmutableMultiMap<K1, V1>) EMPTY;
    }

    public static <K, V> ImmutableMultiMap<K, V> of(K k1, V v1) {
        return new ImmutableMultiMap<>(Map.entry(k1, v1));
    }

    public static <K, V> ImmutableMultiMap<K, V> of(K k1, V v1, K k2, V v2) {
        return new ImmutableMultiMap<>(Map.entry(k1, v1), Map.entry(k2, v2));
    }

    public static <K, V> ImmutableMultiMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3) {
        return new ImmutableMultiMap<>(Map.entry(k1, v1), Map.entry(k2, v2), Map.entry(k3, v3));
    }

    public static <K, V> ImmutableMultiMap<K, V> of(
            K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        return new ImmutableMultiMap<>(
                Map.entry(k1, v1), Map.entry(k2, v2), Map.entry(k3, v3), Map.entry(k4, v4));
    }

    public static <K, V> ImmutableMultiMap<K, V> of(
            K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
        return new ImmutableMultiMap<>(
                Map.entry(k1, v1),
                Map.entry(k2, v2),
                Map.entry(k3, v3),
                Map.entry(k4, v4),
                Map.entry(k5, v5));
    }

    public static <K, V> ImmutableMultiMap<K, V> of(
            K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6) {
        return new ImmutableMultiMap<>(
                Map.entry(k1, v1),
                Map.entry(k2, v2),
                Map.entry(k3, v3),
                Map.entry(k4, v4),
                Map.entry(k5, v5),
                Map.entry(k6, v6));
    }

    public static <K, V> ImmutableMultiMap<K, V> of(
            K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7) {
        return new ImmutableMultiMap<>(
                Map.entry(k1, v1),
                Map.entry(k2, v2),
                Map.entry(k3, v3),
                Map.entry(k4, v4),
                Map.entry(k5, v5),
                Map.entry(k6, v6),
                Map.entry(k7, v7));
    }

    public static <K, V> ImmutableMultiMap<K, V> of(
            K k1,
            V v1,
            K k2,
            V v2,
            K k3,
            V v3,
            K k4,
            V v4,
            K k5,
            V v5,
            K k6,
            V v6,
            K k7,
            V v7,
            K k8,
            V v8) {
        return new ImmutableMultiMap<>(
                Map.entry(k1, v1),
                Map.entry(k2, v2),
                Map.entry(k3, v3),
                Map.entry(k4, v4),
                Map.entry(k5, v5),
                Map.entry(k6, v6),
                Map.entry(k7, v7),
                Map.entry(k8, v8));
    }

    public static <K, V> ImmutableMultiMap<K, V> of(
            K k1,
            V v1,
            K k2,
            V v2,
            K k3,
            V v3,
            K k4,
            V v4,
            K k5,
            V v5,
            K k6,
            V v6,
            K k7,
            V v7,
            K k8,
            V v8,
            K k9,
            V v9) {
        return new ImmutableMultiMap<>(
                Map.entry(k1, v1),
                Map.entry(k2, v2),
                Map.entry(k3, v3),
                Map.entry(k4, v4),
                Map.entry(k5, v5),
                Map.entry(k6, v6),
                Map.entry(k7, v7),
                Map.entry(k8, v8),
                Map.entry(k9, v9));
    }

    public ImmutableMultiMap(ImmutableMultiMap<K, V> other) {
        this.map = other.map;
    }

    public ImmutableMultiMap(Iterable<Map.Entry<K, V>> entries) {
        var tmpMap = new LinkedHashMap<K, List<V>>();
        for (var e : entries) {
            var values = tmpMap.get(e.getKey());
            if (values == null) {
                values = new ArrayList<>();
                tmpMap.put(e.getKey(), values);
            }
            values.add(e.getValue());
        }
        map = new LinkedHashMap<>();
        tmpMap.entrySet().stream().forEach(e -> map.put(e.getKey(), List.copyOf(e.getValue())));
    }

    public ImmutableMultiMap(Map.Entry<K, V>... entries) {
        this(Arrays.asList(entries));
    }

    public List<V> get(K key) {
        var values = map.get(key);
        if (values == null) return List.of();
        return values;
    }

    public boolean containsKey(K key) {
        return map.containsKey(key);
    }

    @Override
    public String toString() {
        return map.toString();
    }

    public String toJsonString() {
        return XJson.asString(map);
    }

    public Optional<V> getFirstParameter(K key) {
        var values = map.get(key);
        if (values == null) return Optional.empty();
        if (values.isEmpty()) return Optional.empty();
        return Optional.of(values.get(0));
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     * @return single value map with only first value included
     */
    public Map<K, V> toMap() {
        return map.entrySet().stream()
                .filter(e -> !e.getValue().isEmpty())
                .collect(Collectors.toMap(Entry::getKey, e -> e.getValue().get(0)));
    }

    public Stream<V> values() {
        return map.values().stream().flatMap(List::stream);
    }

    public Stream<Entry<K, List<V>>> stream() {
        return map.entrySet().stream();
    }

    /** Read only iterator */
    @Override
    public Iterator<Entry<K, List<V>>> iterator() {
        var iter = map.entrySet().iterator();
        return new Iterator<Map.Entry<K, List<V>>>() {
            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }

            @Override
            public Entry<K, List<V>> next() {
                return iter.next();
            }
        };
    }
}
