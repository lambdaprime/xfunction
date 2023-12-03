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

import java.net.http.HttpClient;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Provides additional features for HttpClient.Builder.
 *
 * <p>Requires Java 11 or higher.
 *
 * @author lambdaprime intid@protonmail.com
 */
// Implementing HttpClient.Builder may require to recompile each time
// when new method would be added
public class HttpClientBuilder {

    private static final X509TrustManager TRUST_MANAGER =
            new X509TrustManager() {
                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(
                        java.security.cert.X509Certificate[] certs, String authType) {}

                @Override
                public void checkServerTrusted(
                        java.security.cert.X509Certificate[] certs, String authType) {}
            };

    private final HttpClient.Builder builder;

    public HttpClientBuilder() {
        this(HttpClient.newBuilder());
    }

    public HttpClientBuilder(HttpClient.Builder builder) {
        this.builder = builder;
    }

    /** Makes HttpClient to ignore SSL certificates validation */
    public HttpClientBuilder insecure() {
        TrustManager[] trustAllCerts = new TrustManager[] {TRUST_MANAGER};
        try {
            System.err.println("WARNING! SSL verification is disabled from:");
            var stackTrace = new RuntimeException().getStackTrace();
            var maxLength = 4;
            Arrays.stream(stackTrace).limit(maxLength).forEach(System.err::println);
            if (stackTrace.length > maxLength) System.err.println("...");
            var sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            builder.sslContext(sc);
            // javax.net.ssl.SSLHandshakeException: No subject alternative DNS name matching
            System.setProperty("jdk.internal.httpclient.disableHostnameVerification", "true");
            return this;
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /** Adds to HttpClient TLS1.0 support */
    public HttpClientBuilder tlsv1() {
        var p = new SSLParameters();
        p.setProtocols(new String[] {"TLSv1", "TLSv1.3", "TLSv1.2"});
        builder.sslParameters(p);
        return this;
    }

    /** Return the original HttpClient.Builder from which you can create HttpClient */
    public HttpClient.Builder get() {
        return builder;
    }
}
