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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.stream.Stream;

/**
 * {@link OutputStream} to write data as sequence of bytes and read it back as {@link Stream} of
 * lines.
 *
 * <p>This class blocks any write operations until other thread starts reading lines from it.
 *
 * @author lambdaprime intid@protonmail.com
 */
public class LinesOutputStream extends OutputStream {
    private OutputStream out;
    private BufferedReader in;
    private Stream<String> lines;
    private boolean isClosed;

    public LinesOutputStream() {
        var inPipe = new PipedInputStream(1);
        in = new BufferedReader(new InputStreamReader(inPipe), 1);
        out = Unchecked.get(() -> new PipedOutputStream(inPipe));
        lines = in.lines();
    }

    @Override
    public synchronized void close() throws IOException {
        if (isClosed) return;
        isClosed = true;
        lines.close();
        out.close();
    }

    @Override
    public void write(int b) throws IOException {
        out.write(b);
    }

    @Override
    public void flush() throws IOException {
        out.flush();
    }

    public synchronized Stream<String> lines() {
        Preconditions.isTrue(!isClosed, "Already closed");
        return lines;
    }
}
