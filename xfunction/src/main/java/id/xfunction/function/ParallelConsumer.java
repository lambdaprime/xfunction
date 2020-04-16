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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import id.xfunction.XUtils;


/**
 * <P>Parallel streams may partition the stream in any order which means
 * that there is no guarantee that items which are in the top of the
 * stream will be processed first. Here is an example:</p>
 * 
 * <pre>{@code 
 * AtomicInteger c = new AtomicInteger();
 * Consumer<Integer> consumer = s -> {
 *     if (c.incrementAndGet() < 15)
 *         System.out.println(s);
 * };
 *
 * System.out.println("Parallel stream:");
 * range(0, 1000)
 *     .parallel()
 *     .boxed()
 *     .forEach(consumer);
 * System.out.println("Total: "  + c);
 * }</pre>
 * 
 * May produce:
 * 
 * <pre>
 * Parallel stream:
 * 656
 * 657
 * 658
 * 659
 * 660
 * 661
 * 662
 * 663
 * 664
 * 665
 * 666
 * 667
 * 668
 * 77
 * Total: 1000
 * </pre>
 * 
 * <p>The output of course differs every time you run the code.</p>
 * 
 * <p>Parallel consumer on the other hand allows you to process items of
 * the stream in parallel but guaranteeing that items will be taken in order.</p>
 * 
 * <pre>{@code 
 * System.out.println("Parallel consumer:");
 * c.set(0);
 * try (ParallelConsumer<Integer> pconsumer = new ParallelConsumer<>(consumer)) {
 *     range(0, 1000)
 *         .boxed()
 *         .forEach(pconsumer);
 * }
 * System.out.println("Total: "  + c);
 * }</pre>
 * 
 * May produce:
 * 
 * <pre>
 * Parallel consumer:
 * 0
 * 1
 * 4
 * 5
 * 2
 * 3
 * 6
 * 7
 * 8
 * 9
 * 10
 * 12
 * 13
 * 11
 * Total: 1000
 * </pre>

 * <p>It is achieved by making consumers to take items from the internal
 * queue.</p>
 * 
 * <p>If your application finishes but does not terminate it may be because:</p>
 * 
 * <ul>
 * <li>you are leaking parallel consumers (make sure to close them)</li>
 * <li>one of your consumers still running preventing JVM from stopping</li>
 * </ul>
 * 
 * All exceptions in worker threads by default will be printed to stderr unless the
 * exception handler is defined.
 */
public class ParallelConsumer<T> implements Consumer<T>, AutoCloseable {

    private ExecutorService executor;
    private Consumer<T> consumer;
    private Thread.UncaughtExceptionHandler exHandler = (t, ex) -> {
        XUtils.printExceptions(ex);
    };

    public ParallelConsumer(Consumer<T> consumer) {
        this(consumer, ForkJoinPool.getCommonPoolParallelism());
    }

    public ParallelConsumer(Consumer<T> consumer, int parallelismLevel) {
        this.consumer = consumer;
        this.executor = Executors.newFixedThreadPool(parallelismLevel);
    }

    public ParallelConsumer(Consumer<T> consumer, Thread.UncaughtExceptionHandler exHandler) {
        this(consumer, ForkJoinPool.getCommonPoolParallelism());
        this.exHandler = exHandler;        
    }
    
    @Override
    public void accept(T t) {
        executor.submit(() -> {
            try {
                consumer.accept(t);
            } catch (Throwable e) {
                exHandler.uncaughtException(Thread.currentThread(), e);
            }
        });
    }

    @Override
    public void close() throws Exception {
        executor.shutdown();
        executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);
    }
}
