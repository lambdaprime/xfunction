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

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Allows you to register to the objects which will send you notification once something has
 * changed. Performs single threaded simple notification.
 *
 * <p>Java deprecated Observable since Java 9. The alternatives they propose are:
 *
 * <ul>
 *   <li>javabeans - requires you to add dependency on java.desktop
 *   <li>flow - requires you to define onComplete and others
 *   <li>java.util.concurrent - for messaging between the threads
 * </ul>
 *
 * <p>Which may be not exactly what you need if you are looking for simple notification in non GUI
 * application.
 */
public class XObservable<T> {

    private Set<Consumer<T>> listeners = new HashSet<>();

    public void addListener(Consumer<T> listener) {
        listeners.add(listener);
    }

    public void removeListener(Consumer<T> listener) {
        listeners.remove(listener);
    }

    public void updateAll(T val) {
        listeners.stream().forEach(c -> c.accept(val));
    }
}
