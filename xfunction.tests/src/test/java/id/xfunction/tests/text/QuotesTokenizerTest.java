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
package id.xfunction.tests.text;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import id.xfunction.text.QuotesTokenizer;

public class QuotesTokenizerTest {

    @Test
    public void test_tokenize() {
        assertEquals(Arrays.asList("a", "b", "c"),
            new QuotesTokenizer().tokenize("a   b\tc"));
        
        assertEquals(Arrays.asList("a asdf", "b    c"),
            new QuotesTokenizer().tokenize("\"a asdf\"   \"b    c\""));
        
        assertEquals(Arrays.asList("a \" \" asdf", "b    c"),
            new QuotesTokenizer().tokenize("\"a \\\" \\\" asdf\"   \"b    c\""));
        
        assertEquals(Arrays.asList("a \" \" asdf", "b    c"),
            new QuotesTokenizer().tokenize("'a \" \" asdf'   \"b    c\""));

        assertEquals(Arrays.asList("a", "b c", "d"),
            new QuotesTokenizer().tokenize("a 'b c' d"));

        assertEquals(Arrays.asList("a", "b", "c"),
            new QuotesTokenizer().tokenize("a 'b' c"));

        assertEquals(Arrays.asList("a", "b", "c"),
            new QuotesTokenizer().tokenize("a \"b\" c"));

        assertEquals(Arrays.asList("a", "b\\ c", "d"),
            new QuotesTokenizer().tokenize("a \"b\\\\ c\" d"));

        assertEquals(Arrays.asList("a", "b c", "d"),
            new QuotesTokenizer().tokenize("a \"b c\" d"));

        assertEquals(Arrays.asList("a", "b'c", "d"),
            new QuotesTokenizer().tokenize("a \"b'c\" d"));

        assertEquals(Arrays.asList("a", "b\" c", "d"),
            new QuotesTokenizer().tokenize("a 'b\" c' d"));
        
        assertEquals(Arrays.asList("", "a"),
            new QuotesTokenizer().tokenize("\"\" a"));
        
    }
    
    @Test
    public void test_samples() {
        assertEquals(Arrays.asList("a", "b", "c"),
            new QuotesTokenizer().tokenize("a b c"));

        assertEquals(Arrays.asList("a", "b c"),
            new QuotesTokenizer().tokenize("a \"b c\""));
        
        assertEquals(Arrays.asList("a", "b \"c\" d"),
            new QuotesTokenizer().tokenize("a \"b \\\"c\\\" d\""));
    }
}
