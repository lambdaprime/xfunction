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

    private static final String EQUALS_MESSAGE_FORMAT = "%s: expected value <%s>, actual value <%s>";

    /**
     * Preconditional check for null objects.
     * @throws AssertionError with a message if obj is null
     */
    public static void assertNotNull(Object obj, String message) throws AssertionError {
        if (obj == null) throw new AssertionError(message);
    }

    /**
     * Preconditional check for null objects.
     * @throws AssertionError if obj is null
     */
    public static void assertNotNull(Object obj) throws AssertionError {
        assertNotNull(obj, "");
    }

    /**
     * Preconditional check.
     * @throws AssertionError with a message if b is false
     */
    public static void assertTrue(boolean b, String message) throws AssertionError {
        if (!b) throw new AssertionError(message);
    }

    /**
     * Preconditional check.
     * @throws AssertionError if b is false
     */
    public static void assertTrue(boolean b) throws AssertionError {
        assertTrue(b, "");
    }

    /**
     * Preconditional check for equality.
     * @throws AssertionError if two values are not equal
     */
    public static void assertEquals(int expected, int actual) throws AssertionError {
        assertTrue(expected == actual, String.format("expected value %s, actual value %s", expected, actual));
    }

    /**
     * Preconditional check for equality.
     * @throws AssertionError if two values are not equal
     */
    public static void assertEquals(long expected, long actual) throws AssertionError {
        assertTrue(expected == actual, String.format("expected value %s, actual value %s", expected, actual));
    }

    /**
     * Preconditional check for equality.
     * @throws AssertionError if two values are not equal
     */
    public static void assertEquals(float expected, float actual) throws AssertionError {
        assertTrue(expected == actual, String.format("expected value %s, actual value %s", expected, actual));
    }
    
    /**
     * Preconditional check for equality.
     * @throws AssertionError if two values are not equal
     */
    public static void assertEquals(double expected, double actual) throws AssertionError {
        assertTrue(expected == actual, String.format("expected value %s, actual value %s", expected, actual));
    }
    
    /**
     * Preconditional check for equality.
     * @throws AssertionError if two values are not equal
     */
    public static <T> void assertEquals(T expected, T actual) throws AssertionError {
        assertTrue(Objects.equals(expected, actual), String.format("expected value %s, actual value %s",
            expected, actual));
    }
    
    /**
     * Preconditional check for equality with custom additional message
     * @throws AssertionError if two values are not equal
     */
    public static void assertEquals(int expected, int actual, String msg) throws AssertionError {
        assertTrue(expected == actual, String.format(EQUALS_MESSAGE_FORMAT,
            msg, expected, actual));
    }

    /**
     * Preconditional check for equality with custom additional message
     * @throws AssertionError if two values are not equal
     */
    public static void assertEquals(long expected, long actual, String msg) throws AssertionError {
        assertTrue(expected == actual, String.format(EQUALS_MESSAGE_FORMAT,
            msg, expected, actual));
    }

    /**
     * Preconditional check for equality with custom additional message
     * @throws AssertionError if two values are not equal
     */
    public static void assertEquals(float expected, float actual, String msg) throws AssertionError {
        assertTrue(expected == actual, String.format(EQUALS_MESSAGE_FORMAT,
            msg, expected, actual));
    }
    
    /**
     * Preconditional check for equality with custom additional message
     * @throws AssertionError if two values are not equal
     */
    public static void assertEquals(double expected, double actual, String msg) throws AssertionError {
        assertTrue(expected == actual, String.format(EQUALS_MESSAGE_FORMAT,
            msg, expected, actual));
    }
    
    /**
     * Preconditional check for equality with custom additional message
     * @throws AssertionError if two values are not equal
     */
    public static <T> void assertEquals(T expected, T actual, String msg) throws AssertionError {
        assertTrue(Objects.equals(expected, actual), String.format(EQUALS_MESSAGE_FORMAT,
            msg, expected, actual));
    }
    
}
