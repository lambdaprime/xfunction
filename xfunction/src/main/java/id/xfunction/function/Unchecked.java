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
package id.xfunction.function;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

/**
 * This class allows you to execute functions which throw checked exceptions in a way if they were
 * throwing unchecked RuntimeException.
 *
 * <p>For example given method which throws checked Exception:
 *
 * <pre>{@code
 * int m() throws Exception {
 *     return 0;
 * }
 * }</pre>
 *
 * Instead of writing:
 *
 * <pre>{@code
 * try { m(); } catch (Exception e) { throw new RuntimeException(e); }
 * }</pre>
 *
 * You can use this class:
 *
 * <pre>{@code
 * getInt(this::m);
 * }</pre>
 *
 * @author lambdaprime intid@protonmail.com
 */
public class Unchecked {

    /**
     * Executes given supplier and catch all checked exceptions. If checked exception is thrown it
     * is wrapped into unchecked RuntimeException and is thrown further.
     */
    public static <R, E extends Exception> R get(ThrowingSupplier<R, E> s) {
        try {
            return s.get();
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Executes given int supplier and catch all checked exceptions. If checked exception is thrown
     * it is wrapped into unchecked RuntimeException and is thrown further.
     */
    public static <E extends Exception> int getInt(ThrowingIntSupplier<E> s) {
        try {
            return s.run();
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Executes given boolean supplier and catch all checked exceptions. If checked exception is
     * thrown it is wrapped into unchecked RuntimeException and is thrown further.
     */
    public static <E extends Exception> boolean getBoolean(ThrowingBooleanSupplier<E> s) {
        try {
            return s.run();
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Executes given runnable and catch all checked exceptions. If checked exception is thrown it
     * is wrapped into unchecked RuntimeException and is thrown further.
     */
    public static <E extends Exception> void run(ThrowingRunnable<E> s) {
        try {
            s.run();
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Executes given function with a given argument and catch all checked exceptions. If checked
     * exception is thrown it is wrapped into unchecked RuntimeException and is thrown further.
     */
    public static <A, R, E extends Exception> R apply(ThrowingFunction<A, R, E> s, A a) {
        try {
            return s.apply(a);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Executes given consumer with a given argument and catch all checked exceptions. If checked
     * exception is thrown it is wrapped into unchecked RuntimeException and is thrown further.
     */
    public static <T, E extends Exception> void accept(ThrowingConsumer<T, E> c, T t) {
        try {
            c.accept(t);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Executes given biconsumer with a given arguments and catch all checked exceptions. If checked
     * exception is thrown it is wrapped into unchecked RuntimeException and is thrown further.
     */
    public static <T1, T2, E extends Exception> void accept(
            ThrowingBiConsumer<T1, T2, E> c, T1 t1, T2 t2) {
        try {
            c.accept(t1, t2);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Executes given consumer with a given argument and catch all checked exceptions. If checked
     * exception is thrown it is wrapped into unchecked RuntimeException and is thrown further.
     */
    public static <E extends Exception> void acceptInt(ThrowingIntConsumer<E> c, int t) {
        try {
            c.accept(t);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Accepts ThrowingRunnable which throws checked exception and converts it to runnable which
     * throws unchecked one
     */
    public static <E extends Exception> Runnable wrapRun(ThrowingRunnable<E> r) {
        return () -> run(r);
    }

    /**
     * Accepts ThrowingSupplier which throws checked exception and converts it to Supplier which
     * throws unchecked one
     */
    public static <R, E extends Exception> Supplier<R> wrapGet(ThrowingSupplier<R, E> s) {
        return () -> get(s);
    }

    /**
     * Accepts ThrowingIntSupplier which throws checked exception and converts it to IntSupplier
     * which throws unchecked one
     */
    public static <E extends Exception> IntSupplier wrapGetInt(ThrowingIntSupplier<E> s) {
        return () -> getInt(s);
    }

    /**
     * Accepts ThrowingFunction which throws checked exception and converts it to Function which
     * throws unchecked one
     */
    public static <A, R, E extends Exception> Function<A, R> wrapApply(
            ThrowingFunction<A, R, E> f) {
        return (A a) -> apply(f, a);
    }

    /**
     * Accepts ThrowingConsumer which throws checked exception and converts it to Consumer which
     * throws unchecked one
     */
    public static <T, E extends Exception> Consumer<T> wrapAccept(ThrowingConsumer<T, E> c) {
        return (T t) -> accept(c, t);
    }

    /**
     * Accepts ThrowingConsumer which throws any exception and converts it to Consumer which will
     * catch them and suppress into baseException
     */
    public static <T, E extends Exception> Consumer<T> wrapAccept(
            ThrowingConsumer<T, E> c, Exception baseException) {
        return (T t) -> {
            try {
                c.accept(t);
            } catch (Exception ex) {
                baseException.addSuppressed(ex);
            }
        };
    }

    /**
     * Accepts ThrowingConsumer which throws checked exception and converts it to Consumer which
     * throws unchecked one
     */
    public static <T1, T2, E extends Exception> BiConsumer<T1, T2> wrapAccept(
            ThrowingBiConsumer<T1, T2, E> c) {
        return (T1 t1, T2 t2) -> accept(c, t1, t2);
    }

    /**
     * Accepts ThrowingConsumer which throws checked exception and converts it to Consumer which
     * throws unchecked one
     */
    public static <E extends Exception> IntConsumer wrapAcceptInt(ThrowingIntConsumer<E> c) {
        return (int t) -> acceptInt(c, t);
    }
}
