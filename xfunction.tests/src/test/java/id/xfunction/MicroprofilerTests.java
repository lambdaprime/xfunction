/*
 * Copyright 2019 lambdaprime
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
package id.xfunction;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

public class MicroprofilerTests {

    @Test
    public void test_measureRealTime() {
        Random rand = new Random();
        Consumer<int[]> c = a -> {
            for (int i = 0; i < a.length; i++) {
                a[i] = rand.nextInt();
            }
        };
        Microprofiler profiler = new Microprofiler();
        int[] thousand = new int[1_000];
        int[] million = new int[1_000_000];
        long t1 = profiler.measureRealTime(() -> c.accept(thousand));
        System.out.println(t1);
        long t2 = profiler.measureRealTime(() -> c.accept(million));
        System.out.println(t2);
        // for billion case tests may fail due to OOM
        //int[] billion = new int[1_000_000_000];
        //long t3 = profiler.measureRealTime(() -> c.accept(billion));
        //System.out.println(t3);
        assertTrue(t1 < t2);
    }

    @Test
    public void test_measureRealTime2() {
        Runnable r = () -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {}
        };
        long l = new Microprofiler().measureRealTime(r);
        assertTrue(l >= 100);
    }
    
    @Test
    public void test_measureUserCpuTime_idling() {
        Runnable r = () -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {}
        };
        long l = new Microprofiler().measureUserCpuTime(r);
        assertTrue(l == 0);
    }
    
    @Test
    public void test_measureUserCpuTime() {
        Random rand = new Random();
        Consumer<int[]> c = a -> {
            for (int i = 0; i < a.length; i++) {
                a[i] = rand.nextInt();
                System.out.println(1);
            }
        };
        int[] thousand = new int[100_000];
        long l = new Microprofiler().measureUserCpuTime(() -> c.accept(thousand));
        assertTrue(l > 0);
    }
}
