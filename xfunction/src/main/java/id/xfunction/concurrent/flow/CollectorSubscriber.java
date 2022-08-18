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

import id.xfunction.Preconditions;
import java.util.Collection;

/**
 * Subscriber which collects items to target collection.
 *
 * @author lambdaprime intid@protonmail.com
 */
public class CollectorSubscriber<T, C extends Collection<T>> extends SimpleSubscriber<T> {

    private C targetCollection;

    public CollectorSubscriber(C targetCollection) {
        this.targetCollection = targetCollection;
    }

    @Override
    public void replay(T item) {
        Preconditions.isTrue(!isSubscribed(), "Replay possible only before subscribe");
        targetCollection.add(item);
    }

    @Override
    public void onNext(T item) {
        targetCollection.add(item);
        subscription.request(1);
    }
}
