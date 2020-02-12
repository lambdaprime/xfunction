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

import static java.util.stream.Collectors.joining;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Stream;

/**
 * Set of miscellaneous functions.
 */
public class XUtils {

    /**
     * Returns infinite stream of randomly generated strings with given length
     * @param length strings length
     * @return infinite stream
     */
    public static Stream<String> infiniteRandomStream(int length) {
        InputStream in = new InputStream() {
            Random rand = new Random();
            char[] buf = new char[length];
            int i = length;
            @Override
            public int read() throws IOException {
                if (i == buf.length) {
                    for (int i = 0; i < buf.length - 1; i++) {
                        buf[i] = (char) ('a' + rand.nextInt('z' - 'a'));
                    }
                    buf[length - 1] = '\n';
                    i = 0;
                }
                return buf[i++];
            }
        };
        return new BufferedReader(new InputStreamReader(in)).lines();
    }

    /**
     * Launches background thread which prints JVM memory consumption
     * after given period of time.
     * @param delayMillis
     */
    public static void printMemoryConsumption(long delayMillis) {
        ForkJoinPool.commonPool().submit(() -> {
            long max = 0;
            while (true) {
                long used = (Runtime.getRuntime().totalMemory()
                        - Runtime.getRuntime().freeMemory()) / 1_000_000;
                max = Math.max(used, max);
                System.out.format("Memory used (MB) %s, max peak value: %s\n", used, max);
                try {
                    Thread.sleep(delayMillis);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Calculates md5 sum for input string
     * @return md5 sum
     * @throws Exception
     */
    public static String md5Sum(String string) throws Exception {
        byte[] bytesOfMessage = string.getBytes();
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(bytesOfMessage);
        StringBuilder buf = new StringBuilder();
        for (byte b: digest) {
            if (Byte.toUnsignedInt(b) <= 16) buf.append('0');
            buf.append(Integer.toUnsignedString(Byte.toUnsignedInt(b), 16));
        }
        return buf.toString();
    }

    /**
     * Terminate application with error code
     */
    public static void error(String msg) {
        System.err.println(msg);
        System.exit(1);
    }

    /**
     * Reads given resource file and returns it as a stream
     */
    public static Stream<String> readResourceAsStream(String file) {
        try {
            return new BufferedReader(new InputStreamReader(
                XUtils.class.getResource(file).openStream())).lines();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reads given resource file and returns it as a string
     */
    public static String readResource(String file) {
        return readResourceAsStream(file)
                .collect(joining("\n"));
    }
}
