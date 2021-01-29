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
package id.xfunction.text;

/**
 * <p>Match text with a wildcards (called template) with any other
 * text.</p>
 * 
 * <p>This matcher supports only one possible wildcard symbol which
 * is '*'.</p>
 * 
 * <p>That way you don't need to worry to escape any symbols in your
 * template like in case when using regexps.</p>
 * 
 * <p>For example given template "lol*lol":<p>
 * 
 * <ul>
 * <li>lolasdlol - match</li>
 * <li>looooollol - does not match</li>
 * </ul>
 * 
 * <p>Worst case complexity is O(n^m)</p>
 * 
 */
public class WildcardMatcher {

    private String template;

    public WildcardMatcher(String template) {
        this.template = template;
    }

    /**
     * Check if given text matches current template.
     */
    public boolean matches(String str) {
        return matches(str, 0, 0);
    }

    private boolean matches(String str, int si, int ti) {
        while (si < str.length() && ti < template.length()) {
            if (template.charAt(ti) == '*') {
                boolean ret = matches(str, si, ti + 1);
                if (ret) return true;
                si++;
                continue;
            }
            if (str.charAt(si++) != template.charAt(ti++)) return false;
        }
        if (si == str.length()) {
            if (ti == template.length()) return true;
            if (template.charAt(ti) == '*' && (ti + 1 == template.length())) return true;
        }
        return false;
    }
}
