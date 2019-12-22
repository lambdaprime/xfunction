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

import java.util.function.IntSupplier;
import java.util.function.Supplier;

/**
 * This class allows you to execute functions which throw checked exceptions in a way if they were
 * throwing unchecked RuntimeException.<br>
 * 
 * For example given method which throws checked Exception:<br>
 * 
 * <pre>
 * int m() throws Exception {
 *     return 0;
 * }
 * 
 * </pre>
 * 
 * Instead of writing:<br>
 * 
 * <pre>
 * try { m(); } catch (Exception e) { throw new RuntimeException(e); }
 * 
 * </pre>
 * 
 * You can use this class:<br>
 * 
 * <pre>
 * runUnchecked(this::m);
 * 
 * </pre>
 * 
 */
public class Unchecked {

    /**
     * Executes given supplier and catch all checked exceptions.
     * If checked exception is thrown it is wrapped into unchecked RuntimeException and is
     * thrown further. 
     */
    public static <R, E extends Exception> R runUnchecked(ThrowingSupplier<R, E> s) {
        try {
            return s.run();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Executes given int supplier and catch all checked exceptions.
     * If checked exception is thrown it is wrapped into unchecked RuntimeException and is
     * thrown further. 
     */
    public static <E extends Exception> int runUnchecked(ThrowingIntSupplier<E> s) {
        try {
            return s.run();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Executes given runnable and catch all checked exceptions.
     * If checked exception is thrown it is wrapped into unchecked RuntimeException and is
     * thrown further. 
     */
    public static <E extends Exception> void runUnchecked(ThrowingRunnable<E> s) {
        try {
            s.run();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Accepts ThrowingSupplier which throws checked exception and converts it to
     * Supplier which throws unchecked one
     */
    public static <R, E extends Exception> Supplier<R> wrapGet(ThrowingSupplier<R, E> s) {
        return () -> runUnchecked(s);
    }

    /**
     * Accepts ThrowingIntSupplier which throws checked exception and converts it to
     * IntSupplier which throws unchecked one
     */
    public static <E extends Exception> IntSupplier wrapGetInt(ThrowingIntSupplier<E> s) {
        return () -> runUnchecked(s);
    }

}

