package com.tiagorafaelw.clinic.notification;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(EvolutionApiProperties.class)
public class EvolutionApiConfig {

    @Bean
    RestClient evolutionApiRestClient(EvolutionApiProperties properties) {
        return RestClient.builder()
                .baseUrl(properties.baseUrl())
                .build();
    }
}
