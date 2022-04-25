package org.richard.config;

import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.time.Duration;

public class HttpClientFactory {

    public static HttpClient buildHttpClient() {
        return HttpClient.newBuilder()
            .version(Version.HTTP_1_1)
            .followRedirects(Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(30))
//            .proxy(ProxySelector.of(new InetSocketAddress("proxy.example.com", 80)))
//            .authenticator(Authenticator.getDefault())
            .build();

    }
}
