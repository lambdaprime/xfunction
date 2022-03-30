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
     * @throws PredonditionException with a message if obj is null
     */
    public static void notNull(Object obj, String message) throws PredonditionException {
        if (obj == null) throw new PredonditionException(message);
    }

    /**
     * Preconditional check for null objects.
     *
     * @throws PredonditionException if obj is null
     */
    public static void notNull(Object obj) throws PredonditionException {
        notNull(obj, "");
    }

    /**
     * Preconditional check for null objects.
     *
     * @throws PredonditionException with a message if obj is not null
     */
    public static void isNull(Object obj, String message) throws PredonditionException {
        if (obj != null) throw new PredonditionException(message);
    }

    /**
     * Preconditional check for null objects.
     *
     * @throws PredonditionException if obj is not null
     */
    public static void isNull(Object obj) throws PredonditionException {
        isNull(obj, "");
    }

    /**
     * Preconditional check.
     *
     * @throws PredonditionException with a message if b is false
     */
    public static void isTrue(boolean b, String message) throws PredonditionException {
        if (!b) throw new PredonditionException(message);
    }

    /**
     * Preconditional check.
     *
     * @throws PredonditionException if b is false
     */
    public static void isTrue(boolean b) throws PredonditionException {
        isTrue(b, "");
    }

    /**
     * Preconditional check for equality.
     *
     * @throws PredonditionException if two values are not equal
     */
    public static void equals(long expected, long actual) throws PredonditionException {
        if (expected != actual) throw new PredonditionException(expected, actual);
    }

    /**
     * Preconditional check for equality.
     *
     * @throws PredonditionException if two values are not equal
     */
    public static void equals(double expected, double actual) throws PredonditionException {
        if (expected != actual) throw new PredonditionException(expected, actual);
    }

    /**
     * Preconditional check for equality.
     *
     * @throws PredonditionException if two values are not equal
     */
    public static <T> void equals(T expected, T actual) throws PredonditionException {
        isTrue(
                Objects.equals(expected, actual),
                String.format("expected value %s, actual value %s", expected, actual));
    }

    /**
     * Preconditional check for equality with custom additional message
     *
     * @throws PredonditionException if two values are not equal
     */
    public static void equals(long expected, long actual, String msg) throws PredonditionException {
        if (expected != actual) throw new PredonditionException(msg, expected, actual);
    }

    /**
     * Preconditional check for equality with custom additional message
     *
     * @throws PredonditionException if two values are not equal
     */
    public static void equals(double expected, double actual, String msg)
            throws PredonditionException {
        if (expected != actual) throw new PredonditionException(msg, expected, actual);
    }

    /**
     * Preconditional check for equality with custom additional message
     *
     * @throws PredonditionException if two values are not equal
     */
    public static <T> void equals(T expected, T actual, String msg) throws PredonditionException {
        if (!Objects.equals(expected, actual))
            throw new PredonditionException(msg, expected, actual);
    }

    /**
     * Preconditional check for equality.
     *
     * @throws PredonditionException if two values are not equal
     */
    public static void isLess(long lesser, long greater, String message)
            throws PredonditionException {
        if (lesser >= greater)
            throw new PredonditionException(
                    "%s: Value <%s> should be less <%s>", message, lesser, greater);
    }

    /**
     * Preconditional check for equality.
     *
     * @throws PredonditionException if two values are not equal
     */
    public static void isLessOrEqual(long lesser, long greater, String message)
            throws PredonditionException {
        if (lesser > greater)
            throw new PredonditionException(
                    "%s: Value <%s> should be less or equal <%s>", message, lesser, greater);
    }
}
