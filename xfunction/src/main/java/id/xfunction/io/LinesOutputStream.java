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
package id.xfunction.io;

import id.xfunction.function.Unchecked;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.SynchronousQueue;
import java.util.stream.Stream;

/**
 * Concurrent write of data as sequence of bytes and read it back as {@link Stream} of lines.
 *
 * <p>Allows one thread to write data into this {@link OutputStream} and another to receive such
 * data back as {@link Stream} of lines.
 *
 * <p>{@link OutputStream#write(int)} call blocks until another thread reads the next line.
 *
 * @author lambdaprime intid@protonmail.com
 */
public class LinesOutputStream extends OutputStream {
    public static final String EOQ = new String();
    private StringBuilder buf = new StringBuilder();
    private SynchronousQueue<String> queue = new SynchronousQueue<>();

    @Override
    public void close() throws IOException {
        super.close();
        queue.offer(EOQ);
    }

    @Override
    public void write(int b) throws IOException {
        if (b != '\n') {
            buf.append((char) b);
            return;
        }
        try {
            queue.put(buf.toString());
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        buf.setLength(0);
    }

    public Stream<String> lines() {
        var first = Unchecked.get(() -> queue.take());
        return Stream.iterate(
                        first,
                        l -> l != EOQ,
                        l -> {
                            try {
                                return queue.take();
                            } catch (Exception e) {
                                e.printStackTrace();
                                throw new RuntimeException(e);
                            }
                        })
                .filter(l -> l != EOQ);
    }
}
