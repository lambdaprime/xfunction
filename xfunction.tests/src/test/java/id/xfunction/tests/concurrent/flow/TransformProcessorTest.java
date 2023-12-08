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
package id.xfunction.tests.concurrent.flow;

import id.xfunction.concurrent.SameThreadExecutorService;
import id.xfunction.concurrent.flow.FixedCollectorSubscriber;
import id.xfunction.concurrent.flow.SimpleSubscriber;
import id.xfunction.concurrent.flow.SynchronousPublisher;
import id.xfunction.concurrent.flow.TransformProcessor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.SubmissionPublisher;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author lambdaprime intid@protonmail.com
 */
public class TransformProcessorTest {

    /** 1 -> Pub -> T (changes to "1") -> Sub */
    @Test
    public void test_transform_subscriber() throws Exception {
        var proc = new TransformProcessor<Integer, String>(i -> Optional.of(i.toString()));
        var pub = new SubmissionPublisher<Integer>(Executors.newSingleThreadExecutor(), 10);
        var subscriber = new FixedCollectorSubscriber<>(new ArrayList<String>(), 5);
        proc.subscribe(subscriber);
        pub.subscribe(proc);
        IntStream.range(0, 5).boxed().forEach(pub::submit);
        Assertions.assertEquals("[0, 1, 2, 3, 4]", subscriber.getFuture().get().toString());
    }

    @Test
    public void test_onError() throws Exception {
        var proc =
                new TransformProcessor<Integer, String>(
                        i -> {
                            throw new RuntimeException("test");
                        });
        var pub = new SubmissionPublisher<Integer>(Executors.newSingleThreadExecutor(), 10);
        var future = new CompletableFuture<Throwable>();
        proc.subscribe(
                new SimpleSubscriber<String>() {
                    @Override
                    public void onError(Throwable throwable) {
                        future.complete(throwable);
                    }
                });
        pub.subscribe(proc);
        pub.submit(12);
        Assertions.assertEquals("java.lang.RuntimeException: test", future.get().toString());
        Assertions.assertEquals(1, future.get().getSuppressed().length);
        var suppressed = future.get().getSuppressed()[0];
        Assertions.assertEquals(
                "java.lang.Exception: Original exception belongs to the processor which was created"
                        + " with this stack trace",
                suppressed.toString());
        Assertions.assertEquals(
                true,
                Arrays.toString(suppressed.getStackTrace())
                        .contains(
                                "id.xfunction.tests/id.xfunction.tests.concurrent.flow.TransformProcessorTest.test_onError(TransformProcessorTest.java:"));
    }

    @Test
    public void test_same_thread() throws Exception {
        try (var publisher = new SubmissionPublisher<Integer>(new SameThreadExecutorService(), 1)) {
            var proc = new TransformProcessor<Integer, String>(a -> Optional.of(a.toString()));
            var subscriber = new FixedCollectorSubscriber<>(new ArrayList<String>(), 500);
            proc.subscribe(subscriber);
            publisher.subscribe(proc);
            IntStream.range(0, 500).boxed().forEach(publisher::submit);
            Assertions.assertEquals(
                    IntStream.range(0, 500)
                            .boxed()
                            .map(Object::toString)
                            .collect(Collectors.joining(", ")),
                    subscriber.getFuture().get().toString().replaceAll("\\[|\\]", ""));
        }
    }

    @Test
    public void test_sync_publisher_subscriber() throws Exception {
        try (var publisher = new SynchronousPublisher<Integer>()) {
            var proc = new TransformProcessor<Integer, String>(a -> Optional.of(a.toString()));
            var requestCount = 23;
            var subscriber = new FixedCollectorSubscriber<>(new ArrayList<String>(), requestCount);
            proc.subscribe(subscriber);
            publisher.subscribe(proc);
            Executors.newSingleThreadExecutor()
                    .submit(
                            () -> {
                                IntStream.range(0, 55).boxed().forEach(publisher::submit);
                            });
            Assertions.assertEquals(
                    IntStream.range(0, requestCount)
                            .boxed()
                            .map(Object::toString)
                            .collect(Collectors.joining(", ")),
                    subscriber.getFuture().get().toString().replaceAll("\\[|\\]", ""));
        }
    }

    @Test
    public void test_sync_publisher_subscriber_requests_all() throws Exception {
        try (var publisher = new SynchronousPublisher<Integer>()) {
            var proc = new TransformProcessor<Integer, String>(a -> Optional.of(a.toString()));
            var requestCount = 55;
            var subscriber = new FixedCollectorSubscriber<>(new ArrayList<String>(), requestCount);
            proc.subscribe(subscriber);
            publisher.subscribe(proc);
            IntStream.range(0, requestCount).boxed().forEach(publisher::submit);
            Assertions.assertEquals(
                    IntStream.range(0, requestCount)
                            .boxed()
                            .map(Object::toString)
                            .collect(Collectors.joining(", ")),
                    subscriber.getFuture().get().toString().replaceAll("\\[|\\]", ""));
        }
    }
}
