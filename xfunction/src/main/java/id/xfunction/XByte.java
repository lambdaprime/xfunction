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

public class XByte {

    /**
     * <p>Returns a string representation of the byte array as a
     * sequence of numbers base 16.</p>
     * 
     * <p>Each byte of the array is converted to the code base 16.
     * The code is padded with 0 at the front if its length is
     * less than 2. It means that length of the code is always
     * 2 symbols: a - 0a</p>  
     */
    public static String toHexString(byte[] a) {
        StringBuilder buf = new StringBuilder();
        for (byte b: a) {
            if (Byte.toUnsignedInt(b) <= 16) buf.append('0');
            buf.append(Integer.toUnsignedString(Byte.toUnsignedInt(b), 16));
        }
        return buf.toString();
    }
}
