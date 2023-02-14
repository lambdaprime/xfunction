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
package id.xfunction;

import java.util.Objects;

/** Set of assertions */
public class Preconditions {

    /**
     * Preconditional check for null objects.
     *
     * @throws PreconditionException with a message if obj is null
     */
    public static void notNull(Object obj, String message, Object... param)
            throws PreconditionException {
        if (obj == null) throw new PreconditionException(message, param);
    }

    /**
     * Preconditional check for null objects.
     *
     * @throws PreconditionException if obj is null
     */
    public static void notNull(Object obj) throws PreconditionException {
        notNull(obj, "");
    }

    /**
     * Preconditional check for null objects.
     *
     * @throws PreconditionException with a message if obj is not null
     */
    public static void isNull(Object obj, String message, Object... param)
            throws PreconditionException {
        if (obj != null) throw new PreconditionException(message, param);
    }

    /**
     * Preconditional check for null objects.
     *
     * @throws PreconditionException if obj is not null
     */
    public static void isNull(Object obj) throws PreconditionException {
        isNull(obj, "");
    }

    /**
     * Preconditional check.
     *
     * @throws PreconditionException with a message if b is false
     */
    public static void isTrue(boolean b, String message) throws PreconditionException {
        isTrue(b, "", "");
    }

    /**
     * Preconditional check.
     *
     * @throws PreconditionException with a message if b is false
     */
    public static void isTrue(boolean b, String message, Object... param)
            throws PreconditionException {
        if (!b) throw new PreconditionException(message, param);
    }

    /**
     * Preconditional check.
     *
     * @throws PreconditionException if b is false
     */
    public static void isTrue(boolean b) throws PreconditionException {
        isTrue(b, "");
    }

    /**
     * Preconditional check for equality.
     *
     * @throws PreconditionException if two values are not equal
     */
    public static void equals(long expected, long actual) throws PreconditionException {
        if (expected != actual) throw new PreconditionException(expected, actual);
    }

    /**
     * Preconditional check for equality.
     *
     * @throws PreconditionException if two values are not equal
     */
    public static void equals(double expected, double actual) throws PreconditionException {
        if (expected != actual) throw new PreconditionException(expected, actual);
    }

    /**
     * Preconditional check for equality.
     *
     * @throws PreconditionException if two values are not equal
     */
    public static void equals(Object expected, Object actual) throws PreconditionException {
        isTrue(
                Objects.equals(expected, actual),
                "expected value %s, actual value %s",
                expected,
                actual);
    }

    /**
     * Preconditional check for equality with custom additional message
     *
     * @throws PreconditionException if two values are not equal
     */
    public static void equals(long expected, long actual, String msg) throws PreconditionException {
        if (expected != actual) throw new PreconditionException(msg, expected, actual);
    }

    /**
     * Preconditional check for equality with custom additional message
     *
     * @throws PreconditionException if two values are not equal
     */
    public static void equals(double expected, double actual, String msg)
            throws PreconditionException {
        if (expected != actual) throw new PreconditionException(msg, expected, actual);
    }

    /**
     * Preconditional check for equality with custom additional message
     *
     * @throws PreconditionException if two values are not equal
     */
    public static void equals(Object expected, Object actual, String msg)
            throws PreconditionException {
        if (!Objects.equals(expected, actual))
            throw new PreconditionException(msg, expected, actual);
    }

    /**
     * Preconditional check for equality with custom additional message
     *
     * @throws PreconditionException if two values are not equal
     */
    public static void equals(long expected, long actual, String msg, Object... param)
            throws PreconditionException {
        if (expected != actual)
            throw new PreconditionException(String.format(msg, param), expected, actual);
    }

    /**
     * Preconditional check for equality with custom additional message
     *
     * @throws PreconditionException if two values are not equal
     */
    public static void equals(double expected, double actual, String msg, Object... param)
            throws PreconditionException {
        if (expected != actual)
            throw new PreconditionException(String.format(msg, param), expected, actual);
    }

    /**
     * Preconditional check for equality with custom additional message
     *
     * @throws PreconditionException if two values are not equal
     */
    public static void equals(Object expected, Object actual, String msg, Object... param)
            throws PreconditionException {
        if (!Objects.equals(expected, actual))
            throw new PreconditionException(String.format(msg, param), expected, actual);
    }

    /**
     * Preconditional check for equality.
     *
     * @throws PreconditionException if two values are not equal
     */
    public static void isLess(long lesser, long greater, String message)
            throws PreconditionException {
        if (lesser >= greater)
            throw new PreconditionException(
                    "%s: Value <%s> should be less <%s>", message, lesser, greater);
    }

    /**
     * Preconditional check for equality.
     *
     * @throws PreconditionException if two values are not equal
     */
    public static void isLessOrEqual(long lesser, long greater, String message)
            throws PreconditionException {
        if (lesser > greater)
            throw new PreconditionException(
                    "%s: Value <%s> should be less or equal <%s>", message, lesser, greater);
    }
}
