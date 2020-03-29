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

/**
 * Set of assertions
 */
public class XAsserts {

    /**
     * Preconditional check for null objects.
     * @throws AssertionError is obj is null
     */
    public static void assertNotNull(Object obj, String message) throws AssertionError {
        if (obj == null) throw new AssertionError(message);
    }

    /**
     * Preconditional check for null objects.
     * @throws AssertionError is obj is null
     */
    public static void assertNotNull(Object obj) throws AssertionError {
        assertNotNull(obj, "");
    }

    /**
     * Preconditional check.
     * @throws NullPointerException is obj is null
     */
    public static void assertTrue(boolean b, String message) throws AssertionError {
        if (!b) throw new AssertionError(message);
    }

    /**
     * Preconditional check.
     * @throws NullPointerException is obj is null
     */
    public static void assertTrue(boolean b) throws AssertionError {
        assertTrue(b, "");
    }
}
