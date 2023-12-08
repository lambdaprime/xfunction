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

import id.xfunction.concurrent.SameThreadExecutorService;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.SubmissionPublisher;

/**
 * {@link SubmissionPublisher} which submits all messages sequentially one by one to each
 * subscriber. Queue size of each subscriber is equal to 1 so submitting new messages will block
 * until subscriber`s {@link Subscriber#onNext(Object)} completes.
 *
 * @see SynchronousPublisher for {@link Publisher} with no queues
 * @author lambdaprime intid@protonmail.com
 */
public class SameThreadSubmissionPublisher<T> extends SubmissionPublisher<T> {
    public SameThreadSubmissionPublisher() {
        super(new SameThreadExecutorService(), 1);
    }
}
