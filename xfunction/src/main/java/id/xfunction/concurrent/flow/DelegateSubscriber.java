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
import java.util.Optional;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

/**
 * Subscriber which wraps original subscriber and delegates all calls to it.
 *
 * <p>It allows to override behavior of the original subscriber without redefining all methods of it
 * but only those which are of the interest.
 *
 * @author lambdaprime intid@protonmail.com
 */
public class DelegateSubscriber<T> implements Subscriber<T> {

    private Subscriber<T> subscriber;
    protected Subscription subscription;

    public DelegateSubscriber(Subscriber<T> subscriber) {
        this.subscriber = subscriber;
    }

    @Override
    public void onSubscribe(Subscription subscription) throws PreconditionException {
        Preconditions.isNull(this.subscription, "Already subscribed");
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

    /** Returns subscription if subscriber is already subscribed */
    public Optional<Subscription> getSubscription() {
        return Optional.ofNullable(subscription);
    }
}
