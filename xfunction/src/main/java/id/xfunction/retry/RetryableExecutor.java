/*
 * Copyright 2023 lambdaprime
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
package id.xfunction.retry;

import id.xfunction.function.ThrowingSupplier;
import id.xfunction.lang.XThread;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.function.BooleanSupplier;

/**
 * The retry mechanism which is based on {@link RetryException}.
 *
 * <p>This executor calls user function and every time it throws {@link RetryException} the call
 * repeats. It gives more control to the users on how to handle the retry and what actions no
 * perform between the retries. User can add application specific logging or even depending on
 * certain conditions decide not to retry anymore (not to throw {@link RetryException}).
 *
 * @author lambdaprime intid@protonmail.com
 */
public class RetryableExecutor {

    /**
     * Asynchronous version of {@link #retryIndefinitely(ThrowingSupplier, Duration)} which returns
     * a {@link CompletableFuture} that completes only when supplier returns a value or future is
     * cancelled.
     */
    public <R> CompletableFuture<R> retryIndefinitelyAsync(
            ThrowingSupplier<R, RetryException> supplier, Duration delay) {
        var future = new CompletableFuture<R>();
        ForkJoinPool.commonPool()
                .submit(
                        () -> {
                            future.complete(retry(supplier, delay, () -> !future.isCancelled()));
                        });
        return future;
    }

    /**
     * Calls supplier and returns its value.
     *
     * <p>Every time supplier throws {@link RetryException} it is called again. It repeats
     * indefinitely till supplier either returns a value or throws {@link RuntimeException}.
     */
    public <R> R retryIndefinitely(ThrowingSupplier<R, RetryException> supplier, Duration delay) {
        return retry(supplier, delay, () -> true);
    }

    /**
     * Calls supplier and returns its value.
     *
     * <p>Every time supplier throws {@link RetryException} it is retried again. When maxRetries
     * limit is reached the retry stops and {@link RetryException} is thrown to the caller with
     * {@link RetryException#getCause()} to be the original, last exception, which was thrown by the
     * supplier.
     */
    public <R> R retry(ThrowingSupplier<R, RetryException> supplier, Duration delay, int maxRetries)
            throws RetryException {
        var count = new int[1];
        return retry(supplier, delay, () -> count[0]++ < maxRetries);
    }

    private static <R> R retry(
            ThrowingSupplier<R, RetryException> supplier, Duration delay, BooleanSupplier retryTest)
            throws RetryException {
        RetryException lastRetriedException = null;
        while (retryTest.getAsBoolean()) {
            if (lastRetriedException != null) XThread.sleep(delay.toMillis());
            try {
                return supplier.get();
            } catch (RetryException e) {
                lastRetriedException = e;
            }
        }
        if (lastRetriedException != null) throw lastRetriedException;
        return null;
    }
}
