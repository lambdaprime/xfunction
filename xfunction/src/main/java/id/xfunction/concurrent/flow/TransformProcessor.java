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

import java.util.concurrent.Flow.Processor;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.function.Function;
import id.xfunction.XAsserts;

/**
 * Processor which can be used to transform publisher messages into different
 * type.
 * 
 * <p>
 * Supports only one subscriber.
 * 
 * <p>
 * Example of transforming integer to String:
 * 
 * <pre>
 * 1 -&gt; Publisher -&gt; Transformer (changes to "1") -&gt; Subscriber
 * </pre>
 * 
 * @param <T> input type
 * @param <R> output type
 */
public class TransformProcessor<T, R> implements Processor<T, R> {

    private Subscription subscription;
    private Subscriber<? super R> subscriber;
    private Function<T, R> transformer;

    public TransformProcessor(Function<T, R> transformer) {
        this.transformer = transformer;
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        XAsserts.assertNotNull(this.subscriber, "Transformer must have subscriber which is subscribed to it");
        XAsserts.assertNull(this.subscription, "Already subscribed");
        this.subscription = subscription;
        subscriber.onSubscribe(subscription);
    }

    @Override
    public void onNext(T item) {
        subscriber.onNext(transformer.apply(item));
    }

    @Override
    public void onError(Throwable throwable) {
        subscriber.onError(throwable);
    }

    @Override
    public void onComplete() {
        subscriber.onComplete();
    }

    @Override
    public void subscribe(Subscriber<? super R> subscriber) {
        XAsserts.assertNull(this.subscriber, "Already has subscriber");
        this.subscriber = subscriber;
    }

}
