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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Stream;

import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;

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
        return XByte.toHex(digest);
    }

    /**
     * Calculates md5 sum for a file
     * @return md5 sum
     * @throws Exception
     */
    public static String md5Sum(File f) throws Exception {
        XAsserts.assertTrue(f.isFile(), "Argument " + f + " is not a file");
        try (InputStream bais = new FileInputStream(f)) {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] b = new byte[1<<20];
            int l = 0;
            while ((l = bais.read(b)) != -1) {
                md.update(b, 0, l);
            }
            return XByte.toHex(md.digest());
        }
    }

    /**
     * Reads given resource file and returns its content as a stream of lines.
     * 
     * If you try to read resource from the module you need to "open" that module
     * to xfunction, otherwise Java will throw NPE.
     * 
     * @param absolutePath absolute path to the resource in form "xxx/xxx/.../resource"
     */
    public static Stream<String> readResourceAsStream(String absolutePath) {
        try {
            return new BufferedReader(new InputStreamReader(
                ClassLoader.getSystemResourceAsStream(absolutePath))).lines();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reads given resource file and returns its content as a stream of lines
     * 
     * If you try to read resource from the module you need to "open" that module
     * to xfunction, otherwise Java will throw NPE.
     * 
     * @param clazz class in which package resource is located
     * @param name resource name
     */
    public static Stream<String> readResourceAsStream(Class<?> clazz, String name) {
        try {
            return new BufferedReader(new InputStreamReader(
                clazz.getResourceAsStream(name))).lines();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reads given resource file and returns its content as a string
     * 
     * If you try to read resource from the module you need to "open" that module
     * to xfunction, otherwise Java will throw NPE.
     * 
     * @param absolutePath absolute path to the resource in form "xxx/xxx/.../resource"
     */
    public static String readResource(String absolutePath) {
        return readResourceAsStream(absolutePath)
                .collect(joining("\n"));
    }

    /**
     * Reads given resource file and returns its content as a string
     * 
     * If you try to read resource from the module you need to "open" that module
     * to xfunction, otherwise Java will throw NPE.
     * 
     * @param clazz class in which package resource is located
     * @param name resource name
     */
    public static String readResource(Class<?> clazz, String name) {
        return readResourceAsStream(clazz, name)
                .collect(joining("\n"));
    }

    /**
     * <p>Trims the string and:</p>
     * <ul>
     * <li>If string starts or ends with quote - nothing is done
     * <li>Surrounds it with quotes and returns
     * </ul>
     */
    public static String quote(String s) {
        String st = s.trim();
        if (!st.isEmpty())
            if (st.charAt(0) == '"' || st.charAt(st.length() - 1) == '"')
                return s;
        return "\"" + st + "\"";
    }
    
    /**
     * Trims the string and removes quotes from the head and tail.
     * If string has one quote or no quotes nothing is done. 
     */
    public static String unquote(String s) {
        String st = s.trim();
        if (st.isEmpty()) return s;
        if (st.length() < 2) return s;
        if (st.charAt(0) == '"' && st.charAt(st.length() - 1) == '"')
            return st.substring(1, st.length()-1);
        return s;
    }
    
    /**
     * Prints all exceptions including suppressed ones 
     */
    public static void printExceptions(Throwable ex) {
        concat(of(ex, ex.getCause()), Arrays.stream(ex.getSuppressed()))
            .filter(e -> e != null)
            .forEach(Throwable::printStackTrace);
    }
    
    /**
     * Deletes directory recursively with all files and sub-directories
     */
    public static void deleteDir(Path dir) throws IOException {
        if (!dir.toFile().exists())
            return;
        Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
    
}
