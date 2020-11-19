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

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Splits the string on tokens. Tokens are separated with whitespace or tabs.</p>
 * 
 * <p>For example: "a b c" -&gt; "a", "b", "c"</p>
 * 
 * <p>Any part of substring which is enclosed in quotes considered to be a single token.</p>
 * 
 * <p>For example: "a \"b c\"" -&gt; "a", "b c"</p>
 * 
 * <p>Backslash can be used to escape or cancel the quotes.</p>
 * 
 * <p>For example: "a \"b \\\"c\\\" d\"" -&gt; a", "b \"c\" d"</p>
 * 
 * <p>You can use this class if you need to split command line string on list of arguments.</p>
 * 
 */
public class QuotesTokenizer {

    private List<String> tokens = new ArrayList<String>();
    private StringBuilder buf = new StringBuilder();
    private boolean inside;
    private boolean insideSingleQuotes;

    public List<String> tokenize(String str) {
        int t = 0;
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            boolean isMasked = t % 2 == 1;
            // counting number of \ to know if we mask next
            // symbol or not
            if (ch == '\\') {
                t++;
                if (isMasked)
                    buf.append(ch);
                continue;
            }
            // since it is not \ - reset the counter
            t = 0;
            if (ch == '\'' && !isMasked && !inside) {
                if (insideSingleQuotes) {
                    flush(true);
                }
                insideSingleQuotes = !insideSingleQuotes;
                continue;
            }
            if (ch == '"' && !isMasked && !insideSingleQuotes) {
                if (inside) {
                    flush(true);
                }
                inside = !inside;
                continue;
            }
            if (insideSingleQuotes || inside) {
                buf.append(ch);
                continue;
            }
            if (ch == ' ' || ch == '\t') {
                flush(false);
                continue;
            }
            buf.append(ch);
        }
        if (inside) throw new RuntimeException("Some quote is not closed");
        flush(false);
        return tokens;
    }

    private void flush(boolean force) {
        if (buf.length() == 0 && !force) return;
        tokens.add(buf.toString());
        buf.setLength(0);
    }
}
