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
package id.xfunction.io;

import java.nio.ByteBuffer;

/**
 * @author lambdaprime intid@protonmail.com
 */
public class ByteBufferUtils {

    /**
     * Shifts portion of the buffer between [start, end) to the head of the ByteBuffer. The existing
     * elements will be overwritten.
     *
     * @return same buf with position set to the length of the portion shifted
     */
    public ByteBuffer shiftToHead(ByteBuffer buf, int start, int end) {
        ByteBuffer newBuf = buf.duplicate();
        newBuf.rewind();
        buf.position(start);
        for (int i = start; i < end; i++) {
            newBuf.put(buf.get());
        }
        buf.position(end - start);
        return buf;
    }

    /**
     * If input {@link ByteBuffer} is direct (keeps data in native memory) it will copy all data to
     * byte array (inside Java Heap) and return it. Otherwise it will return {@link
     * ByteBuffer#array()}
     */
    public byte[] asBytes(ByteBuffer data) {
        byte[] b = null;
        if (data.hasArray()) {
            b = data.array();
        } else {
            b = new byte[data.capacity()];
            data.get(b, 0, b.length);
        }
        return b;
    }
}
