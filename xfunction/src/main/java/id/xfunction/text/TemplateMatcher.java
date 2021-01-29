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
 * <p>Imagine you have a template text document and want to compare it to
 * another text documents.</p>
 * 
 * <p>You don't want to do exact comparison and allow only words on certain
 * positions to be different.</p>
 * 
 * <p>You can use regexp .* for that. You escape all meta symbols in your
 * template document and treat it as a regexp. This is pretty straight
 * forward solution and it will work.</p>
 * 
 * <p>But now imagine that this template document is 100 lines long. Then
 * you will have to go through it line by line and escape all the meta
 * symbols again.</p>
 * 
 * <p>This class helps you to deal with such situations by providing you
 * with only one meta symbol * so the only thing which you will need to do
 * is to put it in your template instead of text which should be
 * ignored during matching. It can match text with multiple lines too.</p>
 * 
 * <p>Worst case complexity is O(n^m)</p>
 * 
 */
public class TemplateMatcher {

    private String template;

    public TemplateMatcher(String template) {
        this.template = template;
    }

    /**
     * Check if given string matches current template.
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
