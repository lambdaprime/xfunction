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
package id.xfunction.concurrent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

/**
 * Future which delays its completion for a given amount of time
 *
 * @author lambdaprime intid@protonmail.com
 */
public class DelayedCompletableFuture<T> extends CompletableFuture<T> {

    /**
     * @param millis number of milliseconds when future completes
     */
    public DelayedCompletableFuture(T value, long millis) {
        ForkJoinPool.commonPool()
                .submit(
                        () -> {
                            try {
                                Thread.sleep(millis);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            complete(value);
                        });
    }

    /** Completes a future with a random delay between [startMillis, endMillis) */
    public DelayedCompletableFuture(T value, long startMillis, long endMillis) {
        ForkJoinPool.commonPool()
                .submit(
                        () -> {
                            try {
                                long msec =
                                        (long)
                                                (startMillis
                                                        + (endMillis - startMillis)
                                                                * Math.random());
                                Thread.sleep(msec);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            complete(value);
                        });
    }
}
