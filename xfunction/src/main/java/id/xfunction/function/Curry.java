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

/**
 * Set of functions for implementing currying in Java.
 *
 * <p>For example imagine you have a bifunction like:
 *
 * <pre>{@code
 * String m(String s, Integer i)
 * }</pre>
 *
 * And you want to create a function from it which will be calling m with specific second argument
 * (ex. 5).
 *
 * <p>Instead of writing:
 *
 * <pre>{@code
 * list.stream()
 *     .map(s -> m(s, 5))
 *     .collect(toList());
 * }</pre>
 *
 * You can do:
 *
 * <pre>{@code
 * list.stream()
 *     .map(curry2nd(this.m, 5))
 *     .collect(toList());
 * }</pre>
 *
 * @author lambdaprime intid@protonmail.com
 */
public class Curry {

    /** Curry argument for Consumer */
    public static <T, E extends Exception> ThrowingRunnable<E> curryAccept(
            ThrowingConsumer<T, E> consumer, T v) {
        return () -> consumer.accept(v);
    }

    /** Curry 1st argument for a BiFunction */
    public static <T1, T2, R, E extends Exception> ThrowingFunction<T2, R, E> curryApply1st(
            ThrowingBiFunction<T1, T2, R, E> function, T1 t1) {
        return t2 -> function.apply(t1, t2);
    }

    /** Curry 2nd argument for a BiFunction */
    public static <T1, T2, R, E extends Exception> ThrowingFunction<T1, R, E> curryApply2nd(
            ThrowingBiFunction<T1, T2, R, E> function, T2 t2) {
        return t1 -> function.apply(t1, t2);
    }

    /** Curry 1st argument for a BiConsumer */
    public static <T1, T2, E extends Exception> ThrowingConsumer<T2, E> curryAccept1st(
            ThrowingBiConsumer<T1, T2, E> consumer, T1 t1) {
        return t2 -> consumer.accept(t1, t2);
    }

    /** Curry 2nd argument for a BiConsumer */
    public static <T1, T2, R, E extends Exception> ThrowingConsumer<T1, E> curryAccept2nd(
            ThrowingBiConsumer<T1, T2, E> consumer, T2 t2) {
        return t1 -> consumer.accept(t1, t2);
    }
}
