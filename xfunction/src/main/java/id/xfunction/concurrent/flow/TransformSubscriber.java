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

import java.util.Optional;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.function.Function;

/**
 * Transformer {@link Subscriber} allows to subscribe existing {@link Subscriber} of type R to a
 * {@link Publisher} of type T.
 *
 * <p>The transformation of items from T to R is done with a given transformer {@link Function}
 *
 * @param <T> input type (type of a Publisher)
 * @param <R> output type (type of an existing Subscriber)
 * @author lambdaprime intid@protonmail.com
 */
public class TransformSubscriber<T, R> implements Subscriber<T> {

    private Function<T, Optional<R>> transformer;
    private Subscriber<? super R> targetSubscriber;
    private Subscription subscription;

    /**
     * @param transformer function to transform items from {@link Publisher} to target {@link
     *     Subscriber}. If transformer returns empty message it will be ignored.
     */
    public TransformSubscriber(
            Subscriber<? super R> targetSubscriber, Function<T, Optional<R>> transformer) {
        this.targetSubscriber = targetSubscriber;
        this.transformer = transformer;
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        this.subscription = subscription;
        targetSubscriber.onSubscribe(subscription);
    }

    @Override
    public void onNext(T item) {
        try {
            transformer.apply(item).ifPresent(targetSubscriber::onNext);
        } catch (Exception e) {
            onError(e);
            subscription.cancel();
        }
    }

    @Override
    public void onError(Throwable throwable) {
        targetSubscriber.onError(throwable);
    }

    @Override
    public void onComplete() {
        targetSubscriber.onComplete();
    }
}
