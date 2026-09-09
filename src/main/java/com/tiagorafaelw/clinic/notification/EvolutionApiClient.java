package com.tiagorafaelw.clinic.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Slf4j
@Component
public class EvolutionApiClient {

    private final RestClient restClient;
    private final EvolutionApiProperties properties;

    public EvolutionApiClient(
            RestClient evolutionApiRestClient,
            EvolutionApiProperties properties
    ) {
        this.restClient = evolutionApiRestClient;
        this.properties = properties;
    }

    public void sendTextMessage(String phoneNumber, String text) {
        String normalizedPhoneNumber = normalizePhoneNumber(phoneNumber);

        try {
            restClient.post()
                    .uri("/message/sendText/{instanceName}", properties.instanceName())
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("apikey", properties.apiKey())
                    .body(new SendTextMessageRequest(normalizedPhoneNumber, text))
                    .retrieve()
                    .toBodilessEntity();

            log.info(
                    "Mensagem enviada pela Evolution API. Instância: {}, destino: {}",
                    properties.instanceName(),
                    normalizedPhoneNumber
            );
        } catch (RestClientException exception) {
            log.error(
                    "Não foi possível enviar a mensagem para {} pela Evolution API.",
                    normalizedPhoneNumber,
                    exception
            );

            throw new WhatsAppNotificationException(
                    "Falha ao enviar mensagem pelo WhatsApp.",
                    exception
            );
        }
    }

    private String normalizePhoneNumber(String phoneNumber) {
        String normalizedPhoneNumber = phoneNumber.replaceAll("\\D", "");

        if (normalizedPhoneNumber.isBlank()) {
            throw new IllegalArgumentException("O telefone do paciente é obrigatório.");
        }

        return normalizedPhoneNumber;
    }

    private record SendTextMessageRequest(String number, String text) {
    }
}

