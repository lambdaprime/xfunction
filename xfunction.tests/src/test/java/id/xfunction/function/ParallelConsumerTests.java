/*
 * Copyright 2020 lambdaprime
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import static java.util.stream.IntStream.range;

public class ParallelConsumerTests {

    @Test
    public void test_exception_handler() throws Exception {
        Consumer<String> consumer = s -> {
            throw new RuntimeException();
        };
        boolean[] b = new boolean[1];
        try (ParallelConsumer<String> pconsumer = new ParallelConsumer<>(consumer, (t, ex) -> b[0] = true)) {
            Stream.of("1", "2").forEach(pconsumer);
        }
        assertTrue(b[0]);
    }
    
    @Test
    public void test_consumes_all() throws Exception {
        int COUNT = 1000000;
        AtomicInteger c = new AtomicInteger();
        try (ParallelConsumer<Integer> consumer = new ParallelConsumer<>(s -> c.incrementAndGet())) {
            range(0, COUNT)
                .limit(COUNT)
                .boxed()
                .forEach(consumer);
        }
        assertEquals(COUNT, c.get());
    }

    @Test
    public void test_example() throws Exception {
        AtomicInteger c = new AtomicInteger();
        Consumer<Integer> consumer = s -> {
            if (c.incrementAndGet() < 15)
                System.out.println(s);
        };

        System.out.println("Parallel stream:");
        range(0, 1000)
            .parallel()
            .boxed()
            .forEach(consumer);
        System.out.println("Total: "  + c);

        System.out.println("Parallel consumer:");
        c.set(0);
        try (ParallelConsumer<Integer> pconsumer = new ParallelConsumer<>(consumer)) {
            range(0, 1000)
                .boxed()
                .forEach(pconsumer);
        }
        System.out.println("Total: "  + c);
    }
}
