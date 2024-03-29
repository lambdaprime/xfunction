/*
 * Copyright 2022 lambdaprime
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

import java.util.function.Supplier;

/**
 * Supplier which performs initialization of internal variable when it will be requested for the
 * first time.
 *
 * @author lambdaprime intid@protonmail.com
 */
public class LazyInitializer<T> implements Supplier<T> {
    private T value;
    private Supplier<T> ctor;

    public LazyInitializer(Supplier<T> ctor) {
        this.ctor = ctor;
    }

    @Override
    public T get() {
        if (value == null) {
            synchronized (this) {
                if (value == null) {
                    value = ctor.get();
                }
            }
        }
        return value;
    }

    public void ifInitialized(ThrowingConsumer<T, Exception> consumer) {
        if (value == null) return;
        try {
            consumer.accept(value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return value == null ? "<not yet initialized>" : value.toString();
    }
}
