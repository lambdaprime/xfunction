/*
 * Copyright 2019 lambdaprime
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
package id.xfunction.tests.text;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import id.xfunction.PreconditionException;
import id.xfunction.text.Ellipsizer;
import org.junit.jupiter.api.Test;

public class EllipsizerTests {

    @Test
    public void test_heppy() {
        Ellipsizer ellipsizer = new Ellipsizer(5);
        assertEquals("aaa", ellipsizer.ellipsizeMiddle("aaa"));
        assertEquals("aaaaa", ellipsizer.ellipsizeMiddle("aaaaa"));
        assertEquals("a...a", ellipsizer.ellipsizeMiddle("aaaaaa"));
        assertEquals("a...a", ellipsizer.ellipsizeMiddle("aaaaaaa"));
        assertEquals("aaa", ellipsizer.ellipsizeHead("aaa"));
        assertEquals("aaaaa", ellipsizer.ellipsizeHead("aaaaa"));
        assertEquals("...aa", ellipsizer.ellipsizeHead("aaaaaa"));
        assertEquals("...aa", ellipsizer.ellipsizeHead("aaaaaaa"));

        ellipsizer = new Ellipsizer(6);
        assertEquals("aa...b", ellipsizer.ellipsizeMiddle("aaaaabb"));
        assertEquals("...abb", ellipsizer.ellipsizeHead("...abb"));

        ellipsizer = new Ellipsizer(7);
        assertEquals("aa...cc", ellipsizer.ellipsizeMiddle("aaaaabbbbcccc"));
        assertEquals("...cccc", ellipsizer.ellipsizeHead("aaaaabbbbcccc"));

        ellipsizer = new Ellipsizer(8);
        assertEquals("aaa...cc", ellipsizer.ellipsizeMiddle("aaaaabbbbcccc"));
        assertEquals("...bcccc", ellipsizer.ellipsizeHead("aaaaabbbbcccc"));

        ellipsizer = new Ellipsizer(9);
        assertEquals("aaa...ccc", ellipsizer.ellipsizeMiddle("aaaaabbbbcccc"));
        assertEquals("...bbcccc", ellipsizer.ellipsizeHead("aaaaabbbbcccc"));

        assertThrows(PreconditionException.class, () -> new Ellipsizer(4));

        assertEquals("61...1", new Ellipsizer(6).ellipsizeMiddle("aaaaaaaaaaaaa".getBytes()));
        assertEquals("61...61", new Ellipsizer(7).ellipsizeMiddle("aaaaaaaaaaaaa".getBytes()));
        assertEquals("616...61", new Ellipsizer(8).ellipsizeMiddle("aaaaaaaaaaaaa".getBytes()));
        assertEquals("616...161", new Ellipsizer(9).ellipsizeMiddle("aaaaaaaaaaaaa".getBytes()));
    }
}
