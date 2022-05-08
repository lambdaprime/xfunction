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

import id.xfunction.PreconditionException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.Future;

/** @author lambdaprime intid@protonmail.com */
public class CollectorSubscriber<T> extends SimpleSubscriber<T> {

    private List<T> items;
    private CompletableFuture<List<T>> future = new CompletableFuture<List<T>>();
    private int maxSize;

    public CollectorSubscriber(int maxSize) {
        this.maxSize = maxSize;
        items = new ArrayList<>(maxSize);
    }

    @Override
    public void onSubscribe(Subscription subscription) throws PreconditionException {
        if (items.size() == maxSize) {
            subscription.cancel();
            return;
        }
        super.onSubscribe(subscription);
    }

    @Override
    public void onNext(T item) {
        items.add(item);
        if (items.size() == maxSize) {
            future.complete(items);
            subscription.cancel();
            return;
        }
        subscription.request(1);
    }

    public Future<List<T>> getFuture() {
        return future;
    }
}
