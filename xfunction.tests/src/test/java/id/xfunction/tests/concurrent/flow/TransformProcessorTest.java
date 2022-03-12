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
package id.xfunction.tests.concurrent.flow;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.SubmissionPublisher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import id.xfunction.concurrent.flow.SimpleSubscriber;
import id.xfunction.concurrent.flow.TransformProcessor;

public class TransformProcessorTest {

    /**
     * 1 -> Pub -> T (changes to "1") -> Sub
     */
    @Test
    public void test_transform_subscriber() throws Exception {
        var proc = new TransformProcessor<Integer, String>(i -> i.toString());
        var pub = new SubmissionPublisher<Integer>(Executors.newSingleThreadExecutor(), 10);
        var future = new CompletableFuture<String>();
        proc.subscribe(new SimpleSubscriber<>() {
            @Override
            public void onNext(String item) {
                future.complete(item);
            }
        });
        pub.subscribe(proc);
        var data = 1234;
        pub.submit(data);
        Assertions.assertEquals(future.get(), Integer.toString(data));
    }

}
