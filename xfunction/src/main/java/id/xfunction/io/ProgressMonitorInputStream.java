/*
 * Copyright 2021 lambdaprime
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

/** Allows to monitor {@link InputStream} read progress */
public class ProgressMonitorInputStream extends InputStream {

    private long length;
    private InputStream in;
    private long totalBytesRead;

    public ProgressMonitorInputStream(Path file) throws FileNotFoundException {
        File f = file.toFile();
        this.in = new FileInputStream(f);
        length = f.length();
    }

    public ProgressMonitorInputStream(InputStream in, long lengthInBytes) {
        this.in = in;
        length = lengthInBytes;
    }

    @Override
    public int read() throws IOException {
        totalBytesRead++;
        return in.read();
    }

    /** Returns in % total amount of data which was read from the stream already */
    public int getPercentRead() {
        return (int) (totalBytesRead / (length / 100));
    }

    /** Number of bytes which was read already */
    public long getTotalBytesRead() {
        return totalBytesRead;
    }

    /** Total number of bytes */
    public long getTotalBytes() {
        return length;
    }
}
