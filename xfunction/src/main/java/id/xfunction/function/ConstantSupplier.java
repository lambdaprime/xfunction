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

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Supplier for constant value.
 *
 * <p>It is possible to use ordinary lambda to supply constant values like () -> VALUE but the
 * problem is that calling {@link Object#toString()} for such {@link Supplier} will not show the
 * VALUE inside.
 *
 * @author lambdaprime intid@protonmail.com
 */
public class ConstantSupplier<T> implements Supplier<T> {

    private T value;

    public ConstantSupplier(T value) {
        this.value = value;
    }

    @Override
    public T get() {
        return value;
    }

    @Override
    public String toString() {
        return Objects.toString(value);
    }
}
