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
package id.xfunction.tests.text;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import id.xfunction.text.WildcardMatcher;
import org.junit.jupiter.api.Test;

public class WildcardMatcherTests {

    @Test
    public void test_empty_template() {
        WildcardMatcher matcher = new WildcardMatcher("");
        assertTrue(matcher.matches(""));
        assertFalse(matcher.matches("ffff"));
    }

    @Test
    public void test_empty_string() {
        WildcardMatcher matcher = new WildcardMatcher("lol");
        assertFalse(matcher.matches(""));
        assertFalse(matcher.matches("ffff"));
    }

    @Test
    public void test_matches_no_wildcard() {
        WildcardMatcher matcher = new WildcardMatcher("lol");
        assertTrue(matcher.matches("lol"));
    }

    @Test
    public void test_matches_wildcard() {
        WildcardMatcher matcher = new WildcardMatcher("lol*lol");
        assertTrue(matcher.matches("lolasdlol"));
        assertTrue(matcher.matches("lolasdloalol"));
        assertTrue(matcher.matches("lollol"));
        assertTrue(matcher.matches("lolXlol"));
        assertTrue(matcher.matches("lollollol"));
        assertFalse(matcher.matches("looooollol"));
        assertFalse(matcher.matches("looooollol"));
        assertFalse(matcher.matches("looooolololol"));
        assertFalse(matcher.matches("looooololollol"));
        assertTrue(matcher.matches("lolooololollol"));
    }

    @Test
    public void test_matches_wildcards() {
        WildcardMatcher matcher = new WildcardMatcher("abc*fun*kot");
        assertTrue(matcher.matches("abcccccfunkot"));
        assertTrue(matcher.matches("abcccccfunnnkot"));
        assertTrue(matcher.matches("abcccccfunfunkot"));
        assertTrue(matcher.matches("abcccccfununkolfunnnkot"));
        assertTrue(matcher.matches("abcccccfununkolfunnnkot"));
    }

    @Test
    public void test_matches_everything() {
        WildcardMatcher matcher = new WildcardMatcher("*");
        assertTrue(matcher.matches("abcccccfunkot"));
    }

    @Test
    public void test_matches_wildcard_start() {
        WildcardMatcher matcher = new WildcardMatcher("*fun");
        assertTrue(matcher.matches("abcccccfun"));

        matcher = new WildcardMatcher("*fun*");
        assertTrue(matcher.matches("abcccccfun"));
        assertTrue(matcher.matches("abcccccfunnnn"));
    }

    @Test
    public void test_matches_wildcard_multiline() {
        WildcardMatcher matcher = new WildcardMatcher("is\n*\nending\n");
        assertTrue(matcher.matches("is\nnever\ndfgdfg\nending\n"));
    }

    @Test
    public void test_matches_wildcard_multiline_at_the_end() {
        WildcardMatcher matcher = new WildcardMatcher("is\n*\nending\n*");
        assertTrue(matcher.matches("is\nnever\ndfgdfg\nending\nsfsffff\nggggggg  \n"));
    }
}
