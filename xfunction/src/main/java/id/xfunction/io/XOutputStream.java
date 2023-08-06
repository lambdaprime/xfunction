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

import id.xfunction.XByte;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements an OutputStream by extending ByteArrayOutputStream with methods which allows you to
 * get all written data in one of the formats available.
 *
 * @author lambdaprime intid@protonmail.com
 */
public class XOutputStream extends ByteArrayOutputStream {

    /**
     * Returns written data in form of List of integers. Writing "abc" will result [97, 98, 99] and
     * "hello" to [104, 101, 108, 108, 111]
     */
    public List<Integer> asList() {
        byte[] b = toByteArray();
        List<Integer> output = new ArrayList<>(b.length);
        for (int i = 0; i < b.length; i++) {
            output.add(Byte.toUnsignedInt(b[i]));
        }
        return output;
    }

    /**
     * Returns written data in form of HEX string. Writing "abc" will result to "61, 62, 63" and
     * "hello" to "68, 65, 6c, 6c, 6f".
     *
     * <p>Each byte always encoded with 2 symbols (padded with 0)
     */
    public String asHexString() {
        byte[] b = toByteArray();
        StringBuilder output = new StringBuilder(b.length);
        for (int i = 0; i < b.length; i++) {
            output.append(XByte.toHexPair(b[i]));
            output.append(", ");
        }
        if (output.length() > 0) output.setLength(output.length() - 2);
        return output.toString();
    }
}
