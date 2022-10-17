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
package id.xfunction.text;

import id.xfunction.Preconditions;
import id.xfunction.XByte;

/** Ellipsize the text. For example "aaaaaaaaa" -&gt; "aaaa...aaa". It is thread safe. */
public class Ellipsizer {

    private int maxLength;

    /**
     * @param maxLength Maximum length of the text after it will be ellipsized. Must be greater than
     *     4.
     */
    public Ellipsizer(int maxLength) {
        Preconditions.isTrue(
                maxLength >= 5,
                String.format("maxLength %d is too low and should be at least 5", maxLength));
        this.maxLength = maxLength;
    }

    public String ellipsizeMiddle(String text) {
        if (text.length() <= maxLength) return text;
        int size = maxLength / 2 - 1;
        StringBuilder buf = new StringBuilder();
        buf.append(text, 0, size);
        buf.append("...");
        int start = text.length() - (maxLength - buf.length());
        buf.append(text, start, text.length());
        return buf.toString();
    }

    public String ellipsizeHead(String text) {
        if (text.length() <= maxLength) return text;
        return "..." + text.substring(text.length() - (maxLength - 3));
    }

    public String ellipsizeMiddle(byte[] array) {
        if (array.length * 2 <= maxLength) return XByte.toHex(array);
        var buf = new byte[maxLength / 2 + 1];
        var len1 = (buf.length + 1) / 2;
        System.arraycopy(array, 0, buf, 0, len1);
        var len2 = buf.length / 2;
        System.arraycopy(array, array.length - len2, buf, len1, len2);
        return ellipsizeMiddle(XByte.toHex(buf));
    }
}
