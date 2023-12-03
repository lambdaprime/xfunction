/*
 * Copyright 2023 lambdaprime
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
package id.xfunction;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

public class Checksum {

    /**
     * Calculates md5 sum for byte array and returns it as a HEX string
     *
     * @return md5 sum HEX string
     */
    public static String md5(byte[] data) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(data);
        return XByte.toHex(digest);
    }

    /**
     * Calculates md5 sum for input string and returns it as a HEX string
     *
     * @return md5 sum HEX string
     */
    public static String md5(String string) throws Exception {
        return md5(string.getBytes());
    }

    /**
     * Calculates md5 sum for a file and returns it as a HEX string
     *
     * @return md5 sum HEX string
     */
    public static String md5(File f) throws Exception {
        Preconditions.isTrue(f.isFile(), "Argument " + f + " is not a file");
        try (InputStream bais = new FileInputStream(f)) {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] b = new byte[1 << 20];
            int l = 0;
            while ((l = bais.read(b)) != -1) {
                md.update(b, 0, l);
            }
            return XByte.toHex(md.digest());
        }
    }
}
