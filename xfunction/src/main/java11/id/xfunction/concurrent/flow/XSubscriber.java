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
 * Simple implementation for {@link Subscriber} interface which can be subscribed
 * only once.
 */
public class XSubscriber<T> implements Subscriber<T> {

    private int initNumOfMessages = 1;
    protected Subscription subscription;
    
    /**
     * Allows to set how many messages to request once this subscriber will
     * be first subscribed to some topic. Default number is one.
     */
    public XSubscriber<T> withInitialRequest(int numOfMessages) {
        initNumOfMessages = numOfMessages;
        return this;
    }
    
    /**
     * Saves subscription and requests {@link #withInitialRequest(int)} number of items.
     * @throws XAssertException if subscriber is already subscribed
     */
    @Override
    public void onSubscribe(Subscription subscription) throws XAssertException {
        XAsserts.assertNull(this.subscription, "Already subscribed");
        this.subscription = subscription;
        this.subscription.request(initNumOfMessages);
    }

    /**
     * Empty
     */
    @Override
    public void onNext(T item) {
        
    }

    /**
     * Prints stack trace for the given exception to stderr
     */
    @Override
    public void onError(Throwable throwable) {
        throwable.printStackTrace();
    }

    /**
     * Empty
     */
    @Override
    public void onComplete() {
        
    }

    /**
     * Returns subscription if subscriber is already subscribed
     */
    public Optional<Subscription> getSubscription() {
        return Optional.ofNullable(subscription);
    }
}
