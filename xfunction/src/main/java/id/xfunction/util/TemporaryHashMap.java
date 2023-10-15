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
package id.xfunction.util;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * {@link HashMap} which caches every pair for specified period of time and when it expires such
 * pair is removed.
 *
 * <p>The deletion of expired pairs is passive which means they may not be deleted immediately once
 * they are expired but only during next {@link #put(Object, Object)} or {@link #putAll(Map)}
 * operations. To change it to active users can setup {@link
 * java.util.concurrent.ScheduledExecutorService} to call {@link #cleanupExpired()}.
 *
 * <p>Keys are subject to expiration and not values associated with them. It means if user adds pair
 * "key1": "val1", and later replace value to "key1": "val2" this will not affect this pair
 * expiration time.
 *
 * <p>It is based on {@link HashMap} so all {@link HashMap} properties applies to {@link
 * TemporaryHashMap}.
 *
 * @author lambdaprime intid@protonmail.com
 */
public class TemporaryHashMap<K, V> implements Map<K, V> {

    private Map<K, V> cache = new HashMap<K, V>();
    private LinkedHashMap<K, Long> additionTimestamps = new LinkedHashMap<>();
    private long periodMillis;

    public TemporaryHashMap(Duration period) {
        periodMillis = period.toMillis();
    }

    @Override
    public int size() {
        return cache.size();
    }

    @Override
    public boolean isEmpty() {
        return cache.isEmpty();
    }

    @Override
    public void clear() {
        cache.clear();
        additionTimestamps.clear();
    }

    @Override
    public String toString() {
        return cache.toString();
    }

    @Override
    public boolean containsKey(Object key) {
        return cache.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return cache.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return cache.get(key);
    }

    @Override
    public V put(K key, V value) {
        var prev = putInternal(key, value);
        cleanupExpired();
        return prev;
    }

    private V putInternal(K key, V value) {
        var prev = cache.put(key, value);
        if (prev == null) {
            additionTimestamps.put(key, currentTimeMillis());
        }
        return prev;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (var e : m.entrySet()) {
            putInternal(e.getKey(), e.getValue());
        }
        cleanupExpired();
    }

    /** Returns {@link Collections#unmodifiableSet(Set)} */
    @Override
    public Set<K> keySet() {
        return Collections.unmodifiableSet(cache.keySet());
    }

    /** Returns {@link Collections#unmodifiableCollection(Collection)} */
    @Override
    public Collection<V> values() {
        return Collections.unmodifiableCollection(cache.values());
    }

    /** Returns {@link Collections#unmodifiableSet(Set)} */
    @Override
    public Set<Entry<K, V>> entrySet() {
        return Collections.unmodifiableSet(cache.entrySet());
    }

    @Override
    public V remove(Object key) {
        additionTimestamps.remove(key);
        return cache.remove(key);
    }

    /** Removes expired pairs */
    public void cleanupExpired() {
        if (periodMillis == 0) {
            clear();
            return;
        }
        var iter = additionTimestamps.entrySet().iterator();
        while (iter.hasNext()) {
            var e = iter.next();
            if (e.getValue() >= currentTimeMillis() - periodMillis) break;
            iter.remove();
            cache.remove(e.getKey());
        }
    }

    /** Visible for testing */
    protected long currentTimeMillis() {
        return System.currentTimeMillis();
    }
}
