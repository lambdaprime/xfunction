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

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * Cache implementation in form of a FIFO queue. Ordinary {@link java.util.Queue} does not delete
 * elements automatically - this cache does.
 *
 * <ul>
 *   <li>provides O(1) access time to the items
 *   <li>does not allow duplicates (requires {@link Object#equals(Object)}, {@link
 *       Object#hashCode()})
 *   <li>never exceed its defined maximum size
 * </ul>
 *
 * @author lambdaprime intid@protonmail.com
 */
public class CacheQueue<T> implements Collection<T> {

    private LinkedHashMap<T, Boolean> cache;

    public CacheQueue(int maxSize) {
        cache =
                new LinkedHashMap<T, Boolean>() {
                    @Override
                    protected boolean removeEldestEntry(java.util.Map.Entry<T, Boolean> eldest) {
                        return size() > maxSize;
                    }
                };
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
    public boolean contains(Object o) {
        return cache.containsKey(o);
    }

    @Override
    public Iterator<T> iterator() {
        return cache.keySet().iterator();
    }

    @Override
    public Object[] toArray() {
        return cache.keySet().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return cache.keySet().toArray(a);
    }

    @Override
    public boolean add(T e) {
        return cache.put(e, true) == null;
    }

    @Override
    public boolean remove(Object o) {
        return cache.remove(o) != null;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return cache.keySet().containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return c.stream().map(this::add).filter(r -> r != false).count() > 0;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return c.stream().map(this::remove).filter(r -> r != false).count() > 0;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        cache.clear();
    }

    @Override
    public String toString() {
        return cache.keySet().toString();
    }
}
