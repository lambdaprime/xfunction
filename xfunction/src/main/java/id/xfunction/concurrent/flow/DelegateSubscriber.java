/*
 * Copyright 2022 lambdaprime
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

import java.util.Optional;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

import id.xfunction.XAssertException;
import id.xfunction.XAsserts;

/**
 * Subscriber which wraps original subscriber and delegates all calls to it.
 * 
 * <p>
 * It allows to override behavior of the original subscriber without redefining
 * all methods of it but only those which are of the interest.
 */
public class DelegateSubscriber<T> implements Subscriber<T> {

    private Subscriber<T> subscriber;
    protected Subscription subscription;

    public DelegateSubscriber(Subscriber<T> subscriber) {
        this.subscriber = subscriber;
    }

    @Override
    public void onSubscribe(Subscription subscription) throws XAssertException {
        XAsserts.assertNull(this.subscription, "Already subscribed");
        this.subscription = subscription;
        subscriber.onSubscribe(subscription);
    }

    @Override
    public void onNext(T item) {
        subscriber.onNext(item);
    }

    @Override
    public void onError(Throwable throwable) {
        subscriber.onError(throwable);
    }

    @Override
    public void onComplete() {
        subscriber.onComplete();
    }

    /**
     * Returns subscription if subscriber is already subscribed
     */
    public Optional<Subscription> getSubscription() {
        return Optional.ofNullable(subscription);
    }
}
