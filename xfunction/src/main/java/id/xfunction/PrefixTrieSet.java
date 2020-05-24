/*
 * Copyright 2019 lambdaprime
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

import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.function.Predicate;

/**
 * <p>Set which keeps all elements in prefix trie data structure.</p>
 * 
 * <pre>{@code
 *                        *
 *                      /   \
 *                    h/    t\
 *                    *       *
 *                  e/        e\
 *                  *           *
 *                l/            e\
 *                *               *
 *              l/ \            \0|
 *              /  p\             *
 *             *     *
 *           o/    \0|
 *           *       *
 *         \0|
 *           *
 * }</pre>
 *           
 * <p>Items stored in sorted order.</p>
 * 
 * <p>It implements all methods of Set collection interface except remove.</p>
 * 
 * <p>Complexity is O(L) where L is length of the item.</p>
 * 
 * <p>This collection is not thread safe.</p>
 * 
 */
public class PrefixTrieSet extends AbstractSet<String> {

    private Node root = new Node();
    private int size;
    private boolean isAdded;

    @Override
    public boolean add(String str) {
        XAsserts.assertNotNull(str);
        isAdded = false;
        add(root, (str + '\0').toCharArray(), 0);
        if (isAdded) size++;
        return isAdded;
    }

    @Override
    public Iterator<String> iterator() {
        return new PrefixTrieIterator(root);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeIf(Predicate<? super String> filter) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(Object o) {
        if (o == null) return false;
        if (!(o instanceof String)) return false;
        String s = (String) o;
        return contains(root, s.toCharArray(), 0);
    }

    /**
     * Returns length of the longest item from the current trie
     * which is a prefix of a given string. If no such item exist returns 0
     */
    public int prefixMatches(String str) {
        return prefixMatches(root, str.toCharArray(), 0);
    }

    private int prefixMatches(Node n, char[] a, int i) {
        if (i == a.length) return 0;
        Node next = n.childs.get(a[i]);
        if (next == null) return 0;
        if (next.childs.containsKey('\0')) return 1;
        int ret = prefixMatches(next, a, i + 1);
        return ret == 0? 0: ret + 1;
    }

    // based on DFS
    private boolean contains(Node n, char[] a, int i) {
        if (i == a.length) return n.childs.containsKey('\0');
        if (n == null) return false;
        Node next = n.childs.get(a[i]);
        return contains(next, a, i + 1);
    }

    private Node add(Node cur, char[] a, int i) {
        if (i == a.length) return null;
        if (cur == null)
            cur = new Node();
        Node n = cur.childs.get(a[i]);
        if (!cur.childs.containsKey(a[i]))
            isAdded = true;
        cur.childs.put(a[i], add(n, a, i + 1));
        return cur;
    }

    private static class Node {
        
        // leaf node contains pair <\0, null>
        Map<Character, Node> childs = new TreeMap<>();
        
        @Override
        public String toString() {
            return childs.entrySet().stream()
                    .filter(e -> e.getKey() != '\0')
                    .map(e -> "" + e.getKey())
                    .collect(joining(","));
        }
    }

    private static class PrefixTrieIterator implements Iterator<String> {
        private static final Entry<Character, Node> DELIM = new AbstractMap.SimpleEntry<>('\0', new Node());
        private StringBuilder buf = new StringBuilder();
        private Deque<Entry<Character, Node>> path = new LinkedList<>();

        public PrefixTrieIterator(Node root) {
            path.addAll(root.childs.entrySet());
        }

        @Override
        public boolean hasNext() {
            while (!path.isEmpty()) {
                Entry<Character, Node> e = path.pollFirst();
                if (e == DELIM) {
                    buf.deleteCharAt(buf.length() - 1);
                    continue;
                }
                // stop if we hit leaf node
                if (e.getValue() == null) return true;
                buf.append(e.getKey());
                path.addFirst(DELIM);
                Map<Character, Node> childs = e.getValue().childs;
                Deque<Entry<Character, Node>> tmp = new LinkedList<>();
                tmp.addAll(childs.entrySet());
                tmp.addAll(path);
                path = tmp;
            }
            return false;
        }

        @Override
        public String next() {
            String ret = buf.toString();
            return ret;
        }
    }

}
