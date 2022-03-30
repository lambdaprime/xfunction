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

import id.xfunction.XAsserts;
import java.util.Arrays;
import java.util.concurrent.Flow.Processor;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.SubmissionPublisher;
import java.util.function.Function;

/**
 * Processor which can be used to transform publisher messages into different type.
 *
 * <p>Example of transforming integer to String:
 *
 * <pre>
 * 1 -&gt; Publisher -&gt; Transformer (changes to "1") -&gt; Subscriber
 * </pre>
 *
 * @param <T> input type
 * @param <R> output type
 */
public class TransformProcessor<T, R> extends SubmissionPublisher<R> implements Processor<T, R> {

    private Subscription subscription;
    private Function<T, R> transformer;
    private String ctorStackTrace;

    public TransformProcessor(Function<T, R> transformer) {
        this.transformer = transformer;
        // debugging subscriber issues may be difficult since they operate
        // usually on different threads so stacktrace for them not very useful
        // here we store stacktrace from where it was created initially and
        // include it as a hint
        ctorStackTrace =
                Arrays.stream(new Exception().getStackTrace())
                        .map(StackTraceElement::toString)
                        .collect(joining("\n"));
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        XAsserts.assertNull(
                this.subscription, "Already subscribed. Created from " + ctorStackTrace);
        this.subscription = subscription;
        subscription.request(1);
    }

    @Override
    public void onNext(T item) {
        submit(transformer.apply(item));
        subscription.request(1);
    }

    @Override
    public void onError(Throwable throwable) {
        closeExceptionally(throwable);
    }

    @Override
    public void onComplete() {
        close();
    }
}
