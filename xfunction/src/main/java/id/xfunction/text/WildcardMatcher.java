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

import java.util.List;
import java.util.Optional;

/**
 * Match text with a wildcards (called template) with any other text.
 *
 * <p>This matcher supports only one possible wildcard symbol which is '*'.
 *
 * <p>That way you don't need to worry to escape any symbols in your template like in case when
 * using regexps.
 *
 * <p>For example given template "lol*lol":
 *
 * <ul>
 *   <li>lolasdlol - match
 *   <li>looooollol - does not match
 * </ul>
 *
 * <p>Worst case complexity is O(n^m)
 *
 * @author lambdaprime intid@protonmail.com
 */
public class WildcardMatcher {

    public static class Result {

        /** Template for which matching failed */
        public Optional<String> template = Optional.empty();

        public boolean isMatches() {
            return template.isEmpty();
        }
    }

    private List<String> templates;

    public WildcardMatcher(String template) {
        this.templates = List.of(template);
    }

    public WildcardMatcher(List<String> templates) {
        this.templates = templates;
    }

    /** Check if given text matches all templates. */
    public boolean matches(String str) {
        return match(str).isMatches();
    }

    /** Match given text against all templates and return result */
    public Result match(String str) {
        for (var template : templates) {
            var result = new Result();
            result.template = Optional.of(template);
            if (!matches(template, str, 0, 0)) return result;
        }
        return new Result();
    }

    private boolean matches(String template, String str, int ti, int si) {
        while (si < str.length() && ti < template.length()) {
            if (template.charAt(ti) == '*') {
                boolean ret = matches(template, str, ti + 1, si);
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
