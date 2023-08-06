/*
 * Copyright 2020 lambdaprime
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
package id.xfunction.net;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Static cookie handler with predefined set of cookies which cannot be changed. Requests to add new
 * cookies are ignored.
 *
 * @author lambdaprime intid@protonmail.com
 */
public class StaticCookieHandler extends CookieHandler {

    private static final Pattern PATTERN = Pattern.compile("; ");
    private List<String> cookies;

    /**
     * Creates cookie manager with given cookies.
     *
     * @param cookies string of cookies in format "name=value; name=value; ...; name=value"
     */
    public StaticCookieHandler(String cookies) {
        this(PATTERN.splitAsStream(cookies).collect(toList()));
    }

    /**
     * Creates cookie manager with given list of cookies.
     *
     * @param cookies list of cookies where each cookie in format "name=value"
     */
    public StaticCookieHandler(List<String> cookies) {
        this.cookies = cookies;
    }

    @Override
    public Map<String, List<String>> get(URI uri, Map<String, List<String>> requestHeaders)
            throws IOException {
        Map<String, List<String>> m = new HashMap<>(requestHeaders);
        m.put("Cookie", cookies);
        return Collections.unmodifiableMap(m);
    }

    @Override
    public void put(URI uri, Map<String, List<String>> responseHeaders) throws IOException {}
}
