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

import id.xfunction.PreconditionException;
import id.xfunction.Preconditions;
import java.time.Duration;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Subscriber which collects fixed number of items to target collection.
 *
 * <p>When target collection size reaches maxSize:
 *
 * <ul>
 *   <li>subscription is canceled
 *   <li>future which is maintained by this subscriber completes
 * </ul>
 *
 * <p>Example:
 *
 * <pre>{@code
 * // subscriber collects 5 items to ArrayList and then cancels subscription
 * var subscriber = new FixedCollectorSubscriber<>(new ArrayList<String>(), 5);
 * publisher.subscribe(subscriber);
 * // wait for 5 items and print them to console
 * System.out.println(subscriber.getFuture().get().toString());
 * }</pre>
 *
 * @author lambdaprime intid@protonmail.com
 */
public class FixedCollectorSubscriber<T, C extends Collection<T>> extends SimpleSubscriber<T> {

    private C targetCollection;
    private CompletableFuture<C> future = new CompletableFuture<C>();
    private int maxSize;
    private Consumer<T> consumer = msg -> {};

    public FixedCollectorSubscriber(C targetCollection, int maxSize) {
        this(targetCollection, maxSize, Duration.ofDays(Integer.MAX_VALUE));
    }

    /**
     * @param timeout complete the future by timeout with whatever number of items received into
     *     targetCollection (may be less than maxSize)
     */
    public FixedCollectorSubscriber(C targetCollection, int maxSize, Duration timeout) {
        this.maxSize = maxSize;
        this.targetCollection = targetCollection;
        this.future =
                future.completeOnTimeout(
                        targetCollection, timeout.toMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * Setup additional message consumer.
     *
     * <p>Can be used to print all received messages to {@link System#out}
     *
     * <pre>{@code
     * subscriber.withMessageConsumer(System.out::println);
     * }</pre>
     */
    public FixedCollectorSubscriber<T, C> withMessageConsumer(Consumer<T> consumer) {
        this.consumer = consumer;
        return this;
    }

    @Override
    public void replay(T item) {
        Preconditions.isTrue(!isSubscribed(), "Replay possible only before subscribe");
        if (targetCollection.size() == maxSize) return;
        targetCollection.add(item);
        if (targetCollection.size() == maxSize) {
            future.complete(targetCollection);
        }
    }

    @Override
    public void onSubscribe(Subscription subscription) throws PreconditionException {
        if (targetCollection.size() == maxSize) {
            subscription.cancel();
            return;
        }
        super.onSubscribe(subscription);
    }

    @Override
    public void onNext(T item) {
        targetCollection.add(item);
        consumer.accept(item);
        if (targetCollection.size() == maxSize) {
            future.complete(targetCollection);
            subscription.cancel();
            return;
        }
        subscription.request(1);
    }

    public Future<C> getFuture() {
        return future;
    }
}
