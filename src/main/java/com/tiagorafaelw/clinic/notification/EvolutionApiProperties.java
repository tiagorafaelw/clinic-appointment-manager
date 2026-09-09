package com.tiagorafaelw.clinic.notification;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "evolution.api")
public record EvolutionApiProperties(
        String baseUrl,
        String apiKey,
        String instanceName
) {
}

