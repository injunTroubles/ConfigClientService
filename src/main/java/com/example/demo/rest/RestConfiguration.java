package com.example.demo.rest;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Created by rvann on 7/17/17.
 */
@Configuration
public class RestConfiguration {

    @Bean(name = "configSvcConnMgr")
    PoolingHttpClientConnectionManager configSvcConnMgr(){
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setDefaultMaxPerRoute(5);
        connectionManager.setMaxTotal(10);

        return connectionManager;
    }

    @Bean(name = "configSvcCreds")
    Credentials configSvcCreds(@Value("${spring.cloud.config.username}") String username,
                                  @Value("${spring.cloud.config.password}") String password) {
        return new UsernamePasswordCredentials(username, password);
    }

    @Bean(name = "configRestTemplate")
    RestTemplate configRestTemplate(PoolingHttpClientConnectionManager configSvcConnMgr, Credentials configSvcCreds) {
        CredentialsProvider credProvider = new BasicCredentialsProvider();
        credProvider.setCredentials(AuthScope.ANY, configSvcCreds);

        HttpClient httpClient = HttpClientBuilder
                .create()
                .setDefaultCredentialsProvider(credProvider)
                .setConnectionManager(configSvcConnMgr)
                .addInterceptorFirst(new PreemptiveAuthInterceptor())
                .build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        requestFactory.setConnectTimeout(10000);
        requestFactory.setReadTimeout(10000);

        return new RestTemplate(requestFactory);
    }
}
