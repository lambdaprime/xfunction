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

import id.xfunction.Preconditions;
import id.xfunction.function.Unchecked;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
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
    private Stream<String> stream;
    private boolean isClosed;

    @Override
    public synchronized void close() throws IOException {
        if (isClosed) return;
        super.close();
        isClosed = true;
        if (stream == null) return;
        try {
            queue.put(EOQ);
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        if (stream != null) stream.close();
        isClosed = true;
    }

    @Override
    public void write(int b) throws IOException {
        Preconditions.isTrue(!isClosed, "Already closed");
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

    public synchronized Stream<String> lines() {
        Preconditions.isTrue(!isClosed, "Already closed");
        if (stream != null) return stream;
        var nextLine =
                Unchecked.wrapGet(
                        () -> {
                            var l = "";
                            while ((l = queue.poll(10, TimeUnit.SECONDS)) == null && !isClosed)
                                ;
                            return l != null ? l : EOQ;
                        });
        stream =
                Stream.iterate(
                                nextLine.get(),
                                l -> l != EOQ,
                                l -> {
                                    try {
                                        return nextLine.get();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        throw new RuntimeException(e);
                                    }
                                })
                        .filter(l -> l != EOQ);
        return stream;
    }
}
