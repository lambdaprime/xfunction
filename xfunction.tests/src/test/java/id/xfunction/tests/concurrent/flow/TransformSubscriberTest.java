/*
 * Copyright 2026 lambdaprime
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
import id.xfunction.concurrent.flow.SameThreadSubmissionPublisher;
import id.xfunction.concurrent.flow.TransformSubscriber;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * @author lambdaprime intid@protonmail.com
 */
public class TransformSubscriberTest {

    @Test
    @DisplayName("When transformer returns empty optional it should request more")
    void onNext_emptyOptional_requestsMore() {
        var output = new ArrayList<String>();
        var subscriber =
                new TransformSubscriber<String, String>(
                        new CollectorSubscriber<>(output),
                        a -> {
                            if (Objects.equals(a, "empty")) return Optional.empty();
                            return Optional.of(a);
                        });
        try (var pub = new SameThreadSubmissionPublisher<String>()) {
            pub.subscribe(subscriber);
            pub.submit("a");
            pub.submit("empty");
            pub.submit("a");
            pub.submit("empty");
        }
        Assertions.assertEquals("[a, a]", output.toString());
    }
}
