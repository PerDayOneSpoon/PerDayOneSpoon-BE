package com.sparta.perdayonespoon.config;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;

@Configuration
public class RestTemplateConfig {
    @Bean
    HttpClient httpClient() {
        return HttpClientBuilder.create()
                .setMaxConnTotal(100)    //최대 오픈되는 커넥션 수, 연결을 유지할 최대 숫자
                .setMaxConnPerRoute(30)   //IP, 포트 1쌍에 대해 수행할 커넥션 수, 특정 경로당 최대 숫자
                .build();
    }

    @Bean
    HttpComponentsClientHttpRequestFactory factory(HttpClient httpClient) {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setReadTimeout(5000);
        factory.setConnectTimeout(3000);
        factory.setHttpClient(httpClient);

        return factory;
    }

    @Bean
    RestTemplate restTemplate(HttpComponentsClientHttpRequestFactory factory) {
        return new RestTemplate(factory);
    }
}

//@Configuration
//public class RestTemplateConfig {
//    //HTTP get,post 요청을 날릴때 일정한 형식에 맞춰주는 template
//    @Bean
//    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
//        return restTemplateBuilder
//                .requestFactory(() -> new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()))
//                .additionalMessageConverters(new StringHttpMessageConverter(Charset.forName("UTF-8")))
//                .build();
//    }
//}

