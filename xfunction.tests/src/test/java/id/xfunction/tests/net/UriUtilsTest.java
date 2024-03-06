/*
 * Copyright 2024 lambdaprime
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
package id.xfunction.tests.net;

import id.xfunction.net.UriUtils;
import java.net.PasswordAuthentication;
import java.net.URI;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UriUtilsTest {
    @Test
    public void test() {
        Assertions.assertEquals(
                "c", UriUtils.getResourceName(URI.create("http://a/b/c")).get().toString());
        Assertions.assertEquals(
                "http://user1:passwd@a/b/c",
                UriUtils.injectCredentials(
                                URI.create("http://a/b/c"),
                                new PasswordAuthentication("user1", "passwd".toCharArray()))
                        .toString());
    }
}
