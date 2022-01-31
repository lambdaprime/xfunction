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
package id.xfunction.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import id.xfunction.util.PrefixTrieSet;

public class PrefixTrieTests {

    @Test
    public void test_add() {
        PrefixTrieSet trie = new PrefixTrieSet();
        trie.add("hello");
        trie.add("world");
        trie.add("hello1");
        trie.add("world1");
        trie.add("hello1$");
        trie.add("world1$");
        trie.add("mississippi");
        trie.add("missis");
        trie.add("mis");
        trie.add("mis");
        trie.add("ippi");
        assertEquals(10, trie.size());
        assertEquals("[hello, hello1, hello1$, ippi, mis, missis, mississippi, world, world1, world1$]", trie.toString());
    }
    
    @Test
    public void test_add_same() {
        PrefixTrieSet trie = new PrefixTrieSet();
        boolean ret1 = trie.add("hello");
        boolean ret2 = trie.add("hello");
        boolean ret3 = trie.add("hello");
        assertEquals(1, trie.size());
        assertTrue(ret1);
        assertFalse(ret2);
        assertFalse(ret3);
    }

    @Test
    public void test_size() {
        PrefixTrieSet trie = new PrefixTrieSet();
        trie.add("hello");
        assertEquals(1, trie.size());
        trie.add("hello");
        assertEquals(1, trie.size());
        trie.add("helloo");
        assertEquals(2, trie.size());
        trie.add("hellool");
        assertEquals(3, trie.size());
        trie.add("hellook");
        assertEquals(4, trie.size());
    }

    @Test
    public void test_contains() {
        PrefixTrieSet trie = new PrefixTrieSet();
        List<String> present = Arrays.asList("hello", "help", "word", "world");
        present.stream().forEach(trie::add);
        for (String s : present) {
            assertTrue(trie.contains(s));
        }
        assertFalse(trie.contains("test"));
        assertFalse(trie.contains(null));
        assertTrue(trie.containsAll(present));
        assertFalse(trie.contains("hel"));
    }

    @Test
    public void test_prefixMatch() {
        PrefixTrieSet trie = new PrefixTrieSet();
        List<String> present = Arrays.asList("hello", "help", "word", "world");
        present.stream().forEach(trie::add);
        for (String s : present) {
            assertEquals(s.length(), trie.prefixMatches(s + "asfasdf"));
            assertEquals(s.length(), trie.prefixMatches(s));
        }
        assertEquals(0, trie.prefixMatches("asfasdf"));
        assertEquals(0, trie.prefixMatches("hell"));
    }

    @Test
    public void test_empty() {
        PrefixTrieSet trie = new PrefixTrieSet();
        assertEquals(0, trie.size());
        assertFalse(trie.contains("test"));
        assertEquals(0, trie.prefixMatches("fdd"));
    }
}
