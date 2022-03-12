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
package id.xfunction.tests;

import id.xfunction.XAssertException;
import id.xfunction.XAsserts;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class XAssertsTests {

    @Test
    public void test_assertTrue() {
        Assertions.assertThrows(XAssertException.class, () -> XAsserts.assertTrue(false));
    }

    @Test
    public void test_assertTrue_happy() {
        XAsserts.assertTrue(true);
    }

    @Test
    public void test_assertNotNull() {
        Assertions.assertThrows(XAssertException.class, () -> XAsserts.assertNotNull(null));
    }

    @Test
    public void test_assertNotNull_happy() {
        XAsserts.assertNotNull("test");
    }

    @Test
    public void test_assertEquals() {
        XAsserts.assertEquals(null, null);
        Assertions.assertThrows(XAssertException.class, () -> XAsserts.assertEquals(null, "test"));
    }
}
