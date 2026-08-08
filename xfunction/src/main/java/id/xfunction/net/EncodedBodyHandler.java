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
package id.xfunction.net;

import static java.util.stream.Collectors.joining;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.http.HttpResponse.BodySubscriber;
import java.net.http.HttpResponse.ResponseInfo;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Flow.Subscription;
import java.util.function.Predicate;
import java.util.zip.GZIPInputStream;

/**
 * A {@link BodyHandler} implementation that handles HTTP response bodies with GZIP encoding.
 *
 * <p>This handler automatically detects if the response body is GZIP-encoded (by checking the
 * "content-encoding" header) and decompresses it accordingly. If the body is not GZIP-encoded, it
 * simply returns the body as a string without any processing.
 *
 * @author lambdaprime intid@protonmail.com
 */
class EncodedBodyHandler implements BodyHandler<String> {

    private BodyHandler<String> stringBodyHandler = BodyHandlers.ofString();
    private BodyHandler<byte[]> byteBodyHandler = BodyHandlers.ofByteArray();

    @Override
    public BodySubscriber<String> apply(ResponseInfo responseInfo) {
        if (responseInfo
                .headers()
                .firstValue("content-encoding")
                .map(String::toLowerCase)
                .filter(Predicate.isEqual("gzip"))
                .isEmpty()) return stringBodyHandler.apply(responseInfo);
        BodySubscriber<byte[]> subscriber = byteBodyHandler.apply(responseInfo);
        return new BodySubscriber<String>() {

            @Override
            public void onSubscribe(Subscription subscription) {
                subscriber.onSubscribe(subscription);
            }

            @Override
            public void onNext(List<ByteBuffer> item) {
                subscriber.onNext(item);
            }

            @Override
            public void onError(Throwable throwable) {
                subscriber.onError(throwable);
            }

            @Override
            public void onComplete() {
                subscriber.onComplete();
            }

            @Override
            public CompletionStage<String> getBody() {
                return subscriber.getBody().thenApply(body -> ungzip(body));
            }
        };
    }

    private String ungzip(byte[] body) {
        try {
            return new BufferedReader(
                            new InputStreamReader(
                                    new GZIPInputStream(new ByteArrayInputStream(body))))
                    .lines()
                    .collect(joining("\n"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
