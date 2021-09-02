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
package id.xfunction.util.stream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.stream.Stream;

/**
 * Additions to standard java.util.stream.Stream
 */
public class XStream {

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
}
