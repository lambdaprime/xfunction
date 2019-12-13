package id.xfunction;

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
}
