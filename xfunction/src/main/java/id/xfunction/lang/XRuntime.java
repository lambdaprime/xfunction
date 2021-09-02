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
package id.xfunction.lang;

import id.xfunction.function.ThrowingRunnable;

/**
 * Additions to standard java.lang.Runtime
 */
public class XRuntime {

    /**
     * <p>Registers shutdown hook on Runtime.getRuntime().
     * Equivalent to:</p>
     * 
     * <pre>{@code
     * Runtime.getRuntime().addShutdownHook(new Thread() {
     *     public void run() {
     *         try {
     *             // code
     *         } catch (Exception e) {
     *             e.printStackTrace();
     *         }
     *     }
     * });
     * }</pre>
     * 
     * <p>But much shorter:</p>
     * 
     * <pre>{@code
     * XRuntime.addShutdownHook(() -> {
     *     // code
     * })
     * }</pre>
     * 
     */
    public static Thread addShutdownHook(ThrowingRunnable<Exception> hook) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    hook.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        Runtime.getRuntime().addShutdownHook(thread );
        return thread;
    }
}
