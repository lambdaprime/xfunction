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

import static java.util.stream.Collectors.joining;

import id.xfunction.PreconditionException;
import id.xfunction.Preconditions;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.Flow.Subscription;

/**
 * Simple implementation for {@link Subscriber} interface which can be subscribed only once.
 *
 * @author lambdaprime intid@protonmail.com
 */
public class SimpleSubscriber<T> implements ReplayableSubscriber<T> {

    private int initNumOfMessages = 1;
    protected Subscription subscription;
    private String ctorStackTrace;

    public SimpleSubscriber() {
        // debugging subscriber issues may be difficult since they operate
        // usually on different threads so stacktrace for them not very useful
        // here we store stacktrace from where it was created initially and
        // include it as a hint
        ctorStackTrace =
                Arrays.stream(new Exception().getStackTrace())
                        .map(StackTraceElement::toString)
                        .collect(joining("\n"));
    }

    /**
     * Allows to set how many messages to request once this subscriber will be first subscribed to
     * some topic. Default number is one.
     */
    public SimpleSubscriber<T> withInitialRequest(int numOfMessages) {
        initNumOfMessages = numOfMessages;
        return this;
    }

    /**
     * Saves subscription and requests {@link #withInitialRequest(int)} number of items.
     *
     * @throws PreconditionException if subscriber is already subscribed
     */
    @Override
    public void onSubscribe(Subscription subscription) throws PreconditionException {
        Preconditions.isNull(
                this.subscription, "Already subscribed. Created from " + ctorStackTrace);
        this.subscription = subscription;
        this.subscription.request(initNumOfMessages);
    }

    /** Empty */
    @Override
    public void onNext(T item) {}

    /** Prints stack trace for the given exception to stderr */
    @Override
    public void onError(Throwable throwable) {
        throwable.printStackTrace();
    }

    /** Empty */
    @Override
    public void onComplete() {}

    /** Returns subscription if subscriber is already subscribed */
    public Optional<Subscription> getSubscription() {
        return Optional.ofNullable(subscription);
    }

    /** Empty */
    @Override
    public void replay(T item) {}

    public boolean isSubscribed() {
        return subscription != null;
    }
}
