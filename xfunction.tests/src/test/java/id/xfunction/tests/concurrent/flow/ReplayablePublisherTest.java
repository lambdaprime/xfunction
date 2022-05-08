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

import id.xfunction.concurrent.flow.CollectorSubscriber;
import id.xfunction.concurrent.flow.ReplayablePublisher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/** @author lambdaprime intid@protonmail.com */
public class ReplayablePublisherTest {

    @Test
    public void test_replay() throws Exception {
        try (var publisher = new ReplayablePublisher<Integer>(3)) {
            var subscriber1 = new CollectorSubscriber<Integer>(5);
            var subscriber2 = new CollectorSubscriber<Integer>(5);
            var subscriber3 = new CollectorSubscriber<Integer>(5);
            publisher.subscribe(subscriber1);
            publisher.submit(1);
            publisher.submit(2);
            publisher.subscribe(subscriber2);
            publisher.submit(3);
            publisher.submit(4);
            publisher.subscribe(subscriber3);
            publisher.submit(5);
            publisher.submit(6);
            Assertions.assertEquals("[1, 2, 3, 4, 5]", subscriber1.getFuture().get().toString());
            Assertions.assertEquals("[1, 2, 3, 4, 5]", subscriber2.getFuture().get().toString());
            Assertions.assertEquals("[2, 3, 4, 5, 6]", subscriber3.getFuture().get().toString());
        }
    }

    @Test
    public void test_duplicates() throws Exception {
        try (var publisher = new ReplayablePublisher<Integer>(4)) {
            var subscriber1 = new CollectorSubscriber<Integer>(7);
            var subscriber2 = new CollectorSubscriber<Integer>(3);
            publisher.subscribe(subscriber1);
            publisher.submit(1);
            publisher.submit(2);
            publisher.submit(2);
            publisher.submit(2);
            publisher.subscribe(subscriber2);
            publisher.submit(2);
            publisher.submit(3);
            publisher.submit(4);
            publisher.submit(5);
            publisher.submit(6);
            publisher.submit(7);
            publisher.submit(8);
            Assertions.assertEquals(
                    "[1, 2, 3, 4, 5, 6, 7]", subscriber1.getFuture().get().toString());
            Assertions.assertEquals("[1, 2, 3]", subscriber2.getFuture().get().toString());
        }
    }

    @Test
    public void test_happy() throws Exception {
        try (var publisher = new ReplayablePublisher<Integer>(4)) {
            publisher.submit(1);
            publisher.submit(2);
            publisher.submit(3);
            publisher.submit(4);
            publisher.submit(5);
            var subscriber = new CollectorSubscriber<Integer>(3);
            publisher.subscribe(subscriber);
            Assertions.assertEquals("[2, 3, 4]", subscriber.getFuture().get().toString());
        }
        try (var publisher = new ReplayablePublisher<Integer>(1)) {
            publisher.submit(1);
            var subscriber = new CollectorSubscriber<Integer>(2);
            publisher.subscribe(subscriber);
            publisher.submit(2);
            Assertions.assertEquals("[1, 2]", subscriber.getFuture().get().toString());
        }
    }
}
