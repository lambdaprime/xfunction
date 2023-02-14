/*
 * Copyright 2019 lambdaprime
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
package id.xfunction;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

/** Measures different execution time of functions. */
public class Microprofiler {

    private Optional<ThreadMXBean> mxbean = Optional.empty();

    public Microprofiler() {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        if (threadMXBean.isCurrentThreadCpuTimeSupported()) mxbean = Optional.of(threadMXBean);
    }

    /**
     * Measures real CPU execution time in milliseconds.
     *
     * @return real CPU execution time in milliseconds or -1 if it is not possible to calculate
     */
    public long measureUserCpuTime(Runnable r) {
        if (!mxbean.isPresent()) return -1;
        ThreadMXBean threadMXBean = mxbean.get();
        long s = threadMXBean.getCurrentThreadUserTime();
        r.run();
        return Duration.ofNanos(threadMXBean.getCurrentThreadUserTime() - s).toMillis();
    }

    /**
     * Measures execution time in milliseconds using wall clock. It is not precise time since CPU
     * may perform context switch to another thread but the clock will still be ticking.
     */
    public long measureRealTime(Runnable r) {
        long s = Instant.now().toEpochMilli();
        r.run();
        return Instant.now().toEpochMilli() - s;
    }

    /**
     * Chooses the best available on current JVM way to measure the execution time and returns it in
     * milliseconds.
     */
    public long measureExecutionTime(Runnable r) {
        if (!mxbean.isPresent()) return measureRealTime(r);
        else return measureUserCpuTime(r);
    }

    public static long gcCount() {
        return ManagementFactory.getGarbageCollectorMXBeans().stream()
                .mapToLong(GarbageCollectorMXBean::getCollectionCount)
                .filter(n -> n >= 0)
                .sum();
    }
}
