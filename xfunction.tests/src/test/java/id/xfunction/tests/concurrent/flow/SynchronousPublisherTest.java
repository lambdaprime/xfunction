/*
 * Copyright 2023 lambdaprime
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

import id.xfunction.XUtils;
import id.xfunction.concurrent.flow.CollectorSubscriber;
import id.xfunction.concurrent.flow.FixedCollectorSubscriber;
import id.xfunction.concurrent.flow.SimpleSubscriber;
import id.xfunction.concurrent.flow.SynchronousPublisher;
import id.xfunction.lang.XThread;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

/**
 * @author lambdaprime intid@protonmail.com
 */
public class SynchronousPublisherTest {

    @Test
    public void test_submit_blocks_if_no_subscribers() throws Exception {
        try (var publisher = new SynchronousPublisher<Integer>(); ) {
            var syncQueue = new SynchronousQueue<Boolean>();
            var data = IntStream.range(0, 1000).boxed().toList();
            var items = new ArrayList<Integer>();
            SimpleSubscriber<Integer> subscriber = new CollectorSubscriber<>(items);
            Runnable submitter =
                    () ->
                            Executors.newSingleThreadExecutor()
                                    .submit(
                                            () -> {
                                                try {
                                                    syncQueue.take();
                                                    data.stream().forEach(publisher::submit);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                } finally {
                                                    XUtils.runSafe(() -> syncQueue.put(true));
                                                }
                                            });

            /*
             * Test that submit blocks when no subscribers present
             */
            submitter.run();
            syncQueue.put(true);
            XThread.sleep(100);
            // until now we expect no messages being submitted and so late Subscriber will receive
            // all of
            // them
            publisher.subscribe(subscriber);
            syncQueue.take();
            Assertions.assertEquals(data.toString(), items.toString());

            /*
             * Test that submit blocks when no subscribers with non cancelled subscription present
             */
            subscriber.getSubscription().get().cancel();
            items.clear();
            submitter.run();
            syncQueue.put(true);
            XThread.sleep(100);
            subscriber = new CollectorSubscriber<>(items);
            // until now we expect no messages being submitted and so late Subscriber will receive
            // all of
            // them
            publisher.subscribe(subscriber);
            syncQueue.take();
            Assertions.assertEquals(data.toString(), items.toString());

            /*
             * Test that submit blocks when no subscribers request anything and wakes up when they do
             */
            subscriber.getSubscription().get().cancel();
            items.clear();
            subscriber =
                    new SimpleSubscriber<Integer>() {
                        @Override
                        public void onNext(Integer item) {
                            items.add(item);
                        }
                    };
            submitter.run();
            syncQueue.put(true);
            XThread.sleep(100);
            // until now we expect no messages being submitted and so late Subscriber will receive
            // all of
            // them
            publisher.subscribe(subscriber);
            while (items.isEmpty()) XThread.sleep(100);
            subscriber.getSubscription().get().request(999);
            syncQueue.take();
            Assertions.assertEquals(data.toString(), items.toString());

            /*
             * Test that submit sends only requested number of messages
             */
            subscriber.getSubscription().get().cancel();
            items.clear();
            // request only 1 message
            publisher.subscribe(new FixedCollectorSubscriber<>(items, 1));
            submitter.run();
            syncQueue.put(true);
            XThread.sleep(100);
            publisher.close();
            syncQueue.take();
            Assertions.assertEquals("[0]", items.toString());
        }
    }

    @RepeatedTest(10)
    public void test_concurrent_submit_never_hangs_on_close() throws Exception {
        try (var publisher = new SynchronousPublisher<Integer>(); ) {
            //            var isPublisherActive = new AtomicBoolean();
            var items = new ArrayList<Integer>();
            //            publisher.subscribe(new CollectorSubscriber<>(items));
            Runnable submitter =
                    () ->
                            Executors.newSingleThreadExecutor()
                                    .submit(
                                            () -> {
                                                XThread.sleep((long) (Math.random() * 600));
                                                while (true) publisher.submit(12);
                                            });
            submitter.run();
            XThread.sleep((long) (Math.random() * 600));
            publisher.close();
            System.out.println(items.size());
        }
    }
}
