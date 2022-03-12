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
package id.xfunction.tests.concurrent;

import static java.util.stream.IntStream.range;

import id.xfunction.concurrent.BlockingExecutorService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class BlockingExecutorServiceTest {

    @ParameterizedTest
    @CsvSource({
        "1, 100, 100000",
        "100, 1, 100000",
        "100, 100, 1",
        "100, 10, 100000",
        "1, 1, 100000",
        "11, 1, 100000",
        "11, 7, 100000",
        "200, 7, 100000",
    })
    public void test_different_params(int numOfThreads, int capacity, int total) throws Exception {
        AtomicInteger c = new AtomicInteger();
        ExecutorService executor = new BlockingExecutorService(numOfThreads, capacity);
        range(0, total).forEach(i -> executor.submit(() -> c.incrementAndGet()));
        executor.shutdown();
        executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);
        Assertions.assertEquals(total, c.get());
    }

    @Test
    public void test_shutdown() throws Exception {
        ExecutorService executor = new BlockingExecutorService(100);
        Thread.sleep(1000);
        executor.shutdown();
        executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);
    }
}
