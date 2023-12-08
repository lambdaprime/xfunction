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
import java.util.function.Function;

/**
 * Transformer {@link Publisher} allows to subscribe existing {@link Subscriber} of type R to a
 * {@link Publisher} of type T.
 *
 * <p>The transformation of items from T to R is done with a transformer {@link Function}
 *
 * <p>This publisher class is based on {@link TransformSubscriber} and every {@link
 * #subscribe(Subscriber)} call it wraps into {@link TransformSubscriber} and then subscribes it to
 * the target publisher.
 *
 * @param <T> input type (type of a Publisher)
 * @param <R> output type (type of an existing Subscriber)
 * @author lambdaprime intid@protonmail.com
 */
public class TransformPublisher<T, R> implements Publisher<R> {

    private Function<T, Optional<R>> transformer;
    private Publisher<T> targetPublisher;

    /**
     * @param transformer Transforms input messages from the target {@link Publisher} and publishes
     *     them to subscribers. If transformer returns empty message it will be ignored and not
     *     published.
     */
    public TransformPublisher(Publisher<T> targetPublisher, Function<T, Optional<R>> transformer) {
        this.targetPublisher = targetPublisher;
        this.transformer = transformer;
    }

    @Override
    public void subscribe(Subscriber<? super R> subscriber) {
        targetPublisher.subscribe(new TransformSubscriber<>(subscriber, transformer));
    }
}
