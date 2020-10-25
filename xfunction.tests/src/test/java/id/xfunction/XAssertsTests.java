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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import id.xfunction.XAsserts;

public class XAssertsTests {

    @Test
    public void test_assertTrue() {
        Assertions.assertThrows(AssertionError.class, () -> XAsserts.assertTrue(false));
    }

    @Test
    public void test_assertTrue_happy() {
        XAsserts.assertTrue(true);
    }

    @Test
    public void test_assertNotNull() {
        Assertions.assertThrows(AssertionError.class, () -> XAsserts.assertNotNull(null));
    }

    @Test
    public void test_assertNotNull_happy() {
        XAsserts.assertNotNull("test");
    }
    
    @Test
    public void test_assertEquals() {
        XAsserts.assertEquals(null, null);
        Assertions.assertThrows(AssertionError.class, () -> XAsserts.assertEquals(null, "test"));
    }
}
