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

import id.xfunction.function.ThrowingRunnable;
import java.util.concurrent.ForkJoinPool;

/**
 * Set of miscellaneous functions.
 *
 * @author lambdaprime intid@protonmail.com
 */
public class XUtils {

    /**
     * Launches background thread which prints JVM memory consumption after given period of time.
     */
    public static void printMemoryConsumption(long delayMillis) {
        ForkJoinPool.commonPool()
                .submit(
                        () -> {
                            long max = 0;
                            while (true) {
                                long used =
                                        (Runtime.getRuntime().totalMemory()
                                                        - Runtime.getRuntime().freeMemory())
                                                / 1_000_000;
                                max = Math.max(used, max);
                                System.out.format(
                                        "Memory used (MB) %s, max peak value: %s\n", used, max);
                                try {
                                    Thread.sleep(delayMillis);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
    }

    /**
     * Trims the string and:
     *
     * <ul>
     *   <li>If string starts or ends with quote - nothing is done
     *   <li>Surrounds it with quotes and returns
     * </ul>
     */
    public static String quote(String s) {
        String st = s.trim();
        if (!st.isEmpty()) if (st.charAt(0) == '"' || st.charAt(st.length() - 1) == '"') return s;
        return "\"" + st + "\"";
    }

    /**
     * Trims the string and removes quotes from the head and tail. If string has one quote or no
     * quotes nothing is done.
     */
    public static String unquote(String s) {
        String st = s.trim();
        if (st.isEmpty()) return s;
        if (st.length() < 2) return s;
        if (st.charAt(0) == '"' && st.charAt(st.length() - 1) == '"')
            return st.substring(1, st.length() - 1);
        return s;
    }

    public static boolean isWindows() {
        return System.getProperty("os.name", "").toLowerCase().startsWith("win");
    }

    public static void runSafe(ThrowingRunnable<Exception> runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
