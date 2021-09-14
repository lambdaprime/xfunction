/*
 * Copyright 2021 lambdaprime
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

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Path;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.stream.Stream;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Class which provides wrapping for basic HttpClient operations and takes care
 * of creating HttpRequest with proper BodyHandlers.
 * 
 * Requires Java 11 or higher.
 */
public class XHttpClient {

    /**
     * Default global instance of the client
     */
    public static XHttpClient httpClient = new XHttpClient();

    private HttpClient javaHttpClient = new HttpClientBuilder().insecure().get().build();
    
    
    /**
     * Calls given HTTP API and returns output as Stream of lines.
     */
    public Stream<String> getLines(String api) {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(api))
            .build();
        try {
            return javaHttpClient.send(request, BodyHandlers.ofLines()).body();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getString(String api) {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(api))
            .build();
        try {
            return javaHttpClient.send(request, BodyHandlers.ofString()).body();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Calls given HTTP API and saves output to a given file
     */
    public Path getFile(String api, Path outputFile) {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(api))
            .build();
        try {
            return javaHttpClient.send(request, BodyHandlers.ofFile(outputFile)).body();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
};
