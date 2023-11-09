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

import id.xfunction.logging.TracingToken;
import java.util.Objects;

/**
 * Set of assertions
 *
 * @author lambdaprime intid@protonmail.com
 */
public class Preconditions {

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
     * @throws PreconditionException with a message if obj is null
     */
    public static void notNull(Object obj, String message, Object... param)
            throws PreconditionException {
        notNull(obj, null, message, param);
    }

    /**
     * @param token helps to establish not only stack trace but the object for which precondition
     *     failed
     * @see #notNull(Object, String, Object...)
     */
    public static void notNull(Object obj, TracingToken token, String message, Object... param)
            throws PreconditionException {
        if (obj == null) throw new PreconditionException(format(token, message), param);
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
     * Preconditional check for null objects.
     *
     * @throws PreconditionException with a message if obj is not null
     */
    public static void isNull(Object obj, String message, Object... param)
            throws PreconditionException {
        isNull(obj, null, message, param);
    }

    /**
     * @param token helps to establish not only stack trace but the object for which precondition
     *     failed
     * @see #isNull(Object, String, Object...)
     */
    public static void isNull(Object obj, TracingToken token, String message, Object... param)
            throws PreconditionException {
        if (obj != null) throw new PreconditionException(format(token, message), param);
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
     * Preconditional check.
     *
     * @throws PreconditionException with a message if b is false
     */
    public static void isTrue(boolean b, String message, Object... param)
            throws PreconditionException {
        isTrue(b, null, message, param);
    }

    /**
     * @param token helps to establish not only stack trace but the object for which precondition
     *     failed
     * @see #isTrue(boolean, String, Object...)
     */
    public static void isTrue(boolean b, TracingToken token, String message, Object... param)
            throws PreconditionException {
        if (!b) throw new PreconditionException(format(token, message), param);
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
     * Preconditional check for equality with custom additional message
     *
     * @throws PreconditionException if two values are not equal
     */
    public static void equals(long expected, long actual, String msg) throws PreconditionException {
        equals(expected, actual, null, msg);
    }

    /**
     * @param token helps to establish not only stack trace but the object for which precondition
     *     failed
     * @see #equals(long, long, String)
     */
    public static void equals(long expected, long actual, TracingToken token, String msg)
            throws PreconditionException {
        if (expected != actual)
            throw new PreconditionException(format(token, msg), expected, actual);
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
     * Preconditional check for equality with custom additional message
     *
     * @throws PreconditionException if two values are not equal
     */
    public static void equals(double expected, double actual, String msg)
            throws PreconditionException {
        equals(expected, actual, null, msg);
    }

    /**
     * @param token helps to establish not only stack trace but the object for which precondition
     *     failed
     * @see #equals(double, double, String)
     */
    public static void equals(double expected, double actual, TracingToken token, String msg)
            throws PreconditionException {
        if (expected != actual)
            throw new PreconditionException(format(token, msg), expected, actual);
    }

    /**
     * Preconditional check for equality.
     *
     * @throws PreconditionException if two values are not equal
     */
    public static <T> void equals(T expected, T actual) throws PreconditionException {
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
    public static <T> void equals(T expected, T actual, String msg) throws PreconditionException {
        equals(expected, actual, null, msg);
    }

    /**
     * @param token helps to establish not only stack trace but the object for which precondition
     *     failed
     * @see #equals(Object, Object, String)
     */
    public static <T> void equals(T expected, T actual, TracingToken token, String msg)
            throws PreconditionException {
        if (!Objects.equals(expected, actual))
            throw new PreconditionException(format(token, msg), expected, actual);
    }

    /**
     * Preconditional check for equality with custom additional message
     *
     * @throws PreconditionException if two values are not equal
     */
    public static <T> void equals(T expected, T actual, String msg, Object... param)
            throws PreconditionException {
        equals(expected, actual, null, msg, param);
    }

    /**
     * @param token helps to establish not only stack trace but the object for which precondition
     *     failed
     * @see #equals(Object, Object, String)
     */
    public static <T> void equals(
            T expected, T actual, TracingToken token, String msg, Object... param)
            throws PreconditionException {
        if (!Objects.equals(expected, actual))
            throw new PreconditionException(format(token, msg, param), expected, actual);
    }

    /**
     * Preconditional check for comparison.
     *
     * @throws PreconditionException if two values are not equal
     */
    public static void isLess(long lesser, long greater, String message)
            throws PreconditionException {
        isLess(lesser, greater, null, message);
    }

    /**
     * @param token helps to establish not only stack trace but the object for which precondition
     *     failed
     * @see #isLess(long, long, String)
     */
    public static void isLess(long lesser, long greater, TracingToken token, String message)
            throws PreconditionException {
        if (lesser >= greater)
            throw new PreconditionException(
                    "%s: Value <%s> should be less <%s>", format(token, message), lesser, greater);
    }

    /**
     * Preconditional check for comparison.
     *
     * @throws PreconditionException if two values are not equal
     */
    public static void isLessOrEqual(long lesser, long greater, String message)
            throws PreconditionException {
        isLessOrEqual(lesser, greater, null, message);
    }

    /**
     * @param token helps to establish not only stack trace but the object for which precondition
     *     failed
     * @see #isLessOrEqual(long, long, String)
     */
    public static void isLessOrEqual(long lesser, long greater, TracingToken token, String message)
            throws PreconditionException {
        if (lesser > greater)
            throw new PreconditionException(
                    "%s: Value <%s> should be less or equal <%s>",
                    format(token, message), lesser, greater);
    }

    private static String format(TracingToken token, String message) {
        return token == null ? message : token.toString() + ": " + message;
    }

    private static String format(TracingToken token, String msg, Object[] param) {
        return format(token, String.format(msg, param));
    }
}
