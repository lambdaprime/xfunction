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
package id.xfunction.function;

/**
 * Set of functions for implementing currying in Java
 */
public class Curry {

    /**
     * Curry argument for Consumer
     */
    public static <T, E extends Exception> ThrowingRunnable<E> curry(
            ThrowingConsumer<T, E> consumer, T v) {
        return () -> consumer.accept(v);
    }

    /**
     * Curry 1st argument for a BiFunction
     */
    public static <T1, T2, R, E extends Exception> ThrowingFunction<T2, R, E> curry1st(
            ThrowingBiFunction<T1, T2, R, E> function, T1 t1) {
        return t2 -> function.apply(t1, t2);
    }

    /**
     * Curry 2nd argument for a BiFunction
     */
    public static <T1, T2, R, E extends Exception> ThrowingFunction<T1, R, E> curry2nd(
            ThrowingBiFunction<T1, T2, R, E> function, T2 t2) {
        return t1 -> function.apply(t1, t2);
    }
}
