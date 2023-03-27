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
package id.xfunction.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import id.xfunction.retry.RetryException;
import id.xfunction.retry.RetryableExecutor;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RetryableExecutorTest {
    private RetryableExecutor executor;

    @BeforeEach
    public void setup() {
        executor = new RetryableExecutor();
    }

    @Test
    public void test_retry() throws InterruptedException, ExecutionException {
        var out = new ArrayList<Integer>();
        var res =
                executor.retryIndefinitelyAsync(
                        () -> {
                            out.add(1);
                            if (out.size() < 4) throw new RetryException();
                            return "done";
                        },
                        Duration.ofMillis(1));
        System.out.println(res.toString() + out.toString());
        res.get();
        assertEquals("[1, 1, 1, 1]", out.toString());
    }

    @Test
    public void test_retry_IOException() {
        var out = new ArrayList<Integer>();
        var exception =
                Assertions.assertThrows(
                        RetryException.class,
                        () ->
                                executor.retry(
                                        () -> {
                                            out.add(1);
                                            throw new RetryException(new IOException("test"));
                                        },
                                        Duration.ofMillis(1),
                                        4));

        System.out.println(out.toString());
        assertEquals("[1, 1, 1, 1]", out.toString());
        assertEquals("test", exception.getCause().getMessage());
    }

    @Test
    public void test_retry_eventually_success() {
        var out = new ArrayList<Integer>();
        var res =
                executor.retry(
                        () -> {
                            out.add(1);
                            if (out.size() == 3) return "ok";
                            throw new RetryException(new IOException("test"));
                        },
                        Duration.ofMillis(1),
                        4);

        System.out.println(out.toString());
        assertEquals("[1, 1, 1]", out.toString());
        assertEquals("ok", res);
    }
}
