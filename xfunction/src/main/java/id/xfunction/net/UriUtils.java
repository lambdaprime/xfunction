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
package id.xfunction.net;

import java.net.PasswordAuthentication;
import java.net.URI;
import java.util.Optional;

/**
 * Utilities to work with {@link URI}
 *
 * @author lambdaprime intid@protonmail.com
 */
public class UriUtils {

    public static URI injectCredentials(URI uri, PasswordAuthentication credentials) {
        var credsPair = credentials.getUserName() + ":" + new String(credentials.getPassword());
        return URI.create(uri.toString().replace("://", "://" + credsPair + "@"));
    }

    /**
     * @return empty optional if URI cannot be created
     */
    public Optional<URI> of(String uri) {
        try {
            return Optional.of(new URI(uri.trim()));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * @return last part of the resource path: for example for "http://a/b/c" it returns "c"
     */
    public static Optional<String> getResourceName(URI uri) {
        var path = Optional.ofNullable(uri.getPath()).orElse("").split("/");
        if (path.length == 0) return Optional.empty();
        return Optional.of(path[path.length - 1]);
    }
}
