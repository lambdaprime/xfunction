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

import java.util.Objects;

/**
 * Set of assertions
 */
public class XAsserts {

    /**
     * Preconditional check for null objects.
     * @throws XAssertException with a message if obj is null
     */
    public static void assertNotNull(Object obj, String message) throws XAssertException {
        if (obj == null) throw new XAssertException(message);
    }

    /**
     * Preconditional check for null objects.
     * @throws XAssertException if obj is null
     */
    public static void assertNotNull(Object obj) throws XAssertException {
        assertNotNull(obj, "");
    }

    /**
     * Preconditional check for null objects.
     * @throws XAssertException with a message if obj is not null
     */
    public static void assertNull(Object obj, String message) throws XAssertException {
        if (obj != null) throw new XAssertException(message);
    }

    /**
     * Preconditional check for null objects.
     * @throws XAssertException if obj is not null
     */
    public static void assertNull(Object obj) throws XAssertException {
        assertNull(obj, "");
    }

    /**
     * Preconditional check.
     * @throws XAssertException with a message if b is false
     */
    public static void assertTrue(boolean b, String message) throws XAssertException {
        if (!b) throw new XAssertException(message);
    }

    /**
     * Preconditional check.
     * @throws XAssertException if b is false
     */
    public static void assertTrue(boolean b) throws XAssertException {
        assertTrue(b, "");
    }

    /**
     * Preconditional check for equality.
     * @throws XAssertException if two values are not equal
     */
    public static void assertEquals(long expected, long actual) throws XAssertException {
        if (expected != actual) throw new XAssertException(expected, actual);
    }

    /**
     * Preconditional check for equality.
     * @throws XAssertException if two values are not equal
     */
    public static void assertEquals(double expected, double actual) throws XAssertException {
        if (expected != actual) throw new XAssertException(expected, actual);
    }
    
    /**
     * Preconditional check for equality.
     * @throws XAssertException if two values are not equal
     */
    public static <T> void assertEquals(T expected, T actual) throws XAssertException {
        assertTrue(Objects.equals(expected, actual), String.format("expected value %s, actual value %s",
            expected, actual));
    }

    /**
     * Preconditional check for equality with custom additional message
     * @throws XAssertException if two values are not equal
     */
    public static void assertEquals(long expected, long actual, String msg) throws XAssertException {
        if (expected != actual) throw new XAssertException(msg, expected, actual);
    }

    /**
     * Preconditional check for equality with custom additional message
     * @throws XAssertException if two values are not equal
     */
    public static void assertEquals(double expected, double actual, String msg) throws XAssertException {
        if (expected != actual) throw new XAssertException(msg, expected, actual);
    }
    
    /**
     * Preconditional check for equality with custom additional message
     * @throws XAssertException if two values are not equal
     */
    public static <T> void assertEquals(T expected, T actual, String msg) throws XAssertException {
        if (!Objects.equals(expected, actual)) throw new XAssertException(msg, expected, actual);
    }

    /**
     * Preconditional check for equality.
     * @throws XAssertException if two values are not equal
     */
    public static void assertLess(long lesser, long greater, String message) throws XAssertException {
        if (lesser >= greater) throw new XAssertException("%s: Value <%s> should be less <%s>", message, lesser, greater);
    }

    /**
     * Preconditional check for equality.
     * @throws XAssertException if two values are not equal
     */
    public static void assertLessOrEqual(long lesser, long greater, String message) throws XAssertException {
        if (lesser > greater) throw new XAssertException("%s: Value <%s> should be less or equal <%s>", message, lesser, greater);
    }
}
