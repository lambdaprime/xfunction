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
package id.xfunction.concurrent.flow;

import id.xfunction.Preconditions;
import id.xfunction.XUtils;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Flow;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Publisher which has no internal queues and which delivers items to subscribers synchronously.
 *
 * <p>Items are delivered through {@link Subscriber#onNext(Object)}:
 *
 * <ul>
 *   <li>using same thread where {@link #submit(Object)} method is called
 *   <li>in same order in which subscribers were subscribed to this publisher
 * </ul>
 *
 * <p>This publisher will never loose any item as it will block indefinitely until at least one
 * subscriber requests and receives it.
 *
 * @author lambdaprime intid@protonmail.com
 */
public class SynchronousPublisher<T> implements Flow.Publisher<T>, AutoCloseable {

    private static class MySubscription<T> implements Subscription {
        // needs to be atomic because submit and request can happen concurrently
        private AtomicLong requested = new AtomicLong();
        private boolean isCancelled;
        private Subscriber<? super T> subscriber;
        private Semaphore wakeupSubmit;

        public MySubscription(Subscriber<? super T> subscriber, Semaphore wakeupSubmit) {
            this.subscriber = subscriber;
            this.wakeupSubmit = wakeupSubmit;
        }

        @Override
        public void request(long n) {
            Preconditions.isTrue(!isCancelled, "Already closed");
            requested.addAndGet(n);
            wakeupSubmit.release();
        }

        @Override
        public void cancel() {
            isCancelled = true;
        }
    }

    private List<MySubscription<? super T>> subscriptions = new CopyOnWriteArrayList<>();
    private Semaphore wakeupSubmit = new Semaphore(1);
    private boolean isClosed;

    @Override
    public void subscribe(Subscriber<? super T> subscriber) {
        Preconditions.isTrue(!isClosed, "Already closed");
        var subscription = new MySubscription<>(subscriber, wakeupSubmit);
        subscriber.onSubscribe(subscription);
        subscriptions.add(subscription);
        wakeupSubmit.release();
    }

    /**
     * Publishes the given item to each current subscriber by synchronously invoking its {@link
     * Flow.Subscriber#onNext(Object) onNext} method.
     *
     * <p>If there is no active subscribers, or none of the subscribers requested new items then
     * this method blocks. It will unblock only when one of the following things happens:
     *
     * <ul>
     *   <li>{@link #close()} is called OR
     *   <li>{@link #closeExceptionally(Throwable)} is called OR
     *   <li>one of the {@link Subscriber} requested an item
     * </ul>
     */
    public synchronized void submit(T item) {
        Preconditions.isTrue(!isClosed, "Already closed");
        var wasSubmittedAtLeastOnce = false;
        while (!wasSubmittedAtLeastOnce && !isClosed) {
            try {
                wakeupSubmit.acquire();
                var iter = subscriptions.iterator();
                while (iter.hasNext()) {
                    var subscription = iter.next();
                    if (subscription.isCancelled) {
                        continue;
                    }
                    if (subscription.requested.getAndUpdate(l -> l > 0 ? l - 1 : l) > 0) {
                        try {
                            subscription.subscriber.onNext(item);
                            wasSubmittedAtLeastOnce = true;
                        } catch (Exception e) {
                            XUtils.runSafe(() -> subscription.subscriber.onError(e));
                            subscription.isCancelled = true;
                        }
                    }
                }
                subscriptions.removeIf(s -> s.isCancelled);
                if (!wasSubmittedAtLeastOnce) {
                    wakeupSubmit.acquire();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                wakeupSubmit.release();
            }
        }
    }

    /**
     * Unless already closed, issue {@link Flow.Subscriber#onError(Throwable) onError} to existing
     * subscribers and disallows subsequent {@link #submit(Object)} attempts. Future subscribers
     * also receive the given error.
     */
    public void closeExceptionally(Throwable error) {
        if (isClosed) return;
        isClosed = true;
        subscriptions.forEach(
                subscription -> XUtils.runSafe(() -> subscription.subscriber.onError(error)));
        subscriptions.clear();
        wakeupSubmit.release();
    }

    @Override
    public void close() {
        if (isClosed) return;
        isClosed = true;
        subscriptions.forEach(
                subscription -> XUtils.runSafe(() -> subscription.subscriber.onComplete()));
        subscriptions.clear();
        wakeupSubmit.release();
    }
}
