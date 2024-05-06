/*
 * Copyright 2024 lambdaprime
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
package id.xfunction.tests.io;

import id.xfunction.io.LinesOutputStream;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LinesOutputStreamTest {

    @Test
    public void test() throws Exception {
        var data =
                """
                When the day becomes the night and the sky becomes the sea,
                when the clock strikes heavy and there's no time for tea;
                and in our darkest hour, before my final rhyme,
                she will come back home to Wonderland and turn back the hands of time.
                """
                        .lines()
                        .toList();
        var out = new LinesOutputStream();
        System.out.println(data);
        ForkJoinPool.commonPool()
                .submit(
                        () -> {
                            try {
                                for (var l : data) out.write((l + "\n").getBytes());
                                out.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
        var future = new CompletableFuture<Void>();
        Assertions.assertEquals(data, out.lines().onClose(() -> future.complete(null)).toList());
        future.get();
    }
}
