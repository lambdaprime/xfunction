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

import id.xfunction.util.CacheQueue;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.SubmissionPublisher;

/**
 * Publisher which caches last N submitted items and replays them to new subscribers.
 *
 * <p>Items are replayed before the {@link
 * Subscriber#onSubscribe(java.util.concurrent.Flow.Subscription)}
 *
 * @param <T> input type
 * @author lambdaprime intid@protonmail.com
 */
public class ReplayablePublisher<T> implements Publisher<T>, AutoCloseable {

    private SubmissionPublisher<T> publisher = new SubmissionPublisher<>();
    private CacheQueue<T> cache;

    public ReplayablePublisher(int cacheSize) {
        cache = new CacheQueue<>(cacheSize);
    }

    public ReplayablePublisher(int cacheSize, Executor executor, int maxBufferCapacity) {
        this(cacheSize);
        publisher = new SubmissionPublisher<>(executor, maxBufferCapacity);
    }

    public void submit(T item) {
        if (!cache.add(item)) return;
        publisher.submit(item);
    }

    public void subscribe(ReplayableSubscriber<? super T> subscriber) {
        cache.forEach(subscriber::replay);
        publisher.subscribe(subscriber);
    }

    @Override
    public void subscribe(Subscriber<? super T> subscriber) {
        publisher.subscribe(subscriber);
    }

    @Override
    public void close() {
        publisher.close();
    }

    public int getNumberOfItemsInCache() {
        return cache.size();
    }

    public boolean isCacheEmpty() {
        return cache.isEmpty();
    }

    public boolean cacheContains(T o) {
        return cache.contains(o);
    }

    public void clearCache() {
        cache.clear();
    }

    public Collection<T> getCacheReadOnly() {
        return Collections.unmodifiableCollection(cache);
    }

    public boolean isSubscribed(Subscriber<? super T> subscriber) {
        return publisher.isSubscribed(subscriber);
    }
}
