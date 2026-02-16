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

import static java.util.stream.Collectors.toList;

import id.xfunction.function.Unchecked;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * BlockingExecutorService keeps pool of worker threads which read tasks from the blocking queue.
 *
 * <p>If blocking queue is bounded then thread submitting a new task to this executor will block
 * until new space in queue became available (with standard ThreadPoolExecutor such task will be
 * rejected).
 *
 * @author lambdaprime intid@protonmail.com
 */
public class BlockingExecutorService extends AbstractExecutorService {

    // end of queue
    private static final Runnable EOQ = () -> {};

    private Semaphore semaphore;
    private volatile boolean isShutdown;
    private BlockingQueue<Runnable> queue;
    private List<WorkerThread> workers;

    private class WorkerThread extends Thread {
        @Override
        public void run() {
            try {
                semaphore.acquire();
                Runnable r;
                while ((r = queue.take()) != EOQ) {
                    //                    System.out.println("pick up new item from queue");
                    r.run();
                }
                // put it back for other workers
                queue.put(EOQ);
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                semaphore.release();
            }
        }
    }

    /**
     * Creates executor with bounded queue of given capacity.
     *
     * @param maximumPoolSize number of worker threads which will be created and be waiting for a
     *     new tasks
     * @param capacity size of the internal queue from which worker will pick up the tasks
     */
    public BlockingExecutorService(int maximumPoolSize, int capacity) {
        this.queue = new ArrayBlockingQueue<>(capacity);
        this.semaphore = new Semaphore(maximumPoolSize);
        this.workers =
                Stream.generate(() -> new WorkerThread())
                        .peek(Thread::start)
                        .limit(maximumPoolSize)
                        .collect(toList());
    }

    public BlockingExecutorService(int capacity) {
        this(ForkJoinPool.getCommonPoolParallelism(), capacity);
    }

    @Override
    public void shutdown() {
        isShutdown = true;
        Executors.defaultThreadFactory()
                .newThread(
                        () -> {
                            Unchecked.run(() -> queue.put(EOQ));
                        })
                .start();
    }

    @Override
    public List<Runnable> shutdownNow() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isShutdown() {
        return isShutdown;
    }

    @Override
    public boolean isTerminated() {
        return semaphore.availablePermits() == workers.size();
    }

    /**
     * This call blocks indefinitely if there are some pending tasks in the queue which is yet to be
     * taken.
     *
     * <p>If the queue is empty it behaves as {@link ExecutorService#awaitTermination(long,
     * TimeUnit)}
     */
    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        boolean isTerminated = false;
        int numOfPermits = workers.size();
        while (true) {
            isTerminated = semaphore.tryAcquire(numOfPermits, timeout, unit);
            if (isTerminated && queue.peek() != EOQ) {
                semaphore.release(numOfPermits);
            } else break;
        }
        if (isTerminated) return true;
        return workers.stream().map(Thread::isAlive).noneMatch(Predicate.isEqual(true));
    }

    @Override
    public void execute(Runnable command) {
        if (isShutdown) return;
        Unchecked.run(() -> queue.put(command));
    }
}
