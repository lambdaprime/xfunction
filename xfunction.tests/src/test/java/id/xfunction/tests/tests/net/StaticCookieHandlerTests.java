/*
 * Copyright 2020 lambdaprime
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
package id.xfunction.tests.tests.net;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import id.xfunction.net.StaticCookieHandler;

public class StaticCookieHandlerTests {

    @Test
    public void test_cookies_string() throws IOException {
        StaticCookieHandler handler = new StaticCookieHandler("name1=value1; name2=value2; name3=value3");
        Map<String, List<String>> requestHeaders = new HashMap<>();
        requestHeaders = handler.get(null, requestHeaders);
        System.out.println(requestHeaders);
        assertEquals("{Cookie=[name1=value1, name2=value2, name3=value3]}", requestHeaders.toString());
    }

    @Test
    public void test_cookies_list() throws IOException {
        StaticCookieHandler handler = new StaticCookieHandler(Arrays.asList(
            "name1=value11", "name2=value2", "name3=value3"));
        Map<String, List<String>> requestHeaders = new HashMap<>();
        requestHeaders = handler.get(null, requestHeaders);
        System.out.println(requestHeaders);
        assertEquals("{Cookie=[name1=value11, name2=value2, name3=value3]}", requestHeaders.toString());
    }
}
