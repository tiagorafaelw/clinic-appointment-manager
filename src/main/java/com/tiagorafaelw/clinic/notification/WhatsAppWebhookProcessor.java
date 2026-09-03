package com.tiagorafaelw.clinic.notification;

import com.tiagorafaelw.clinic.appointment.AppointmentService;
import com.tiagorafaelw.clinic.appointment.AppointmentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WhatsAppWebhookProcessor {

    private final AppointmentService appointmentService;

    public void processIncomingMessage(String patientPhone, String incomingText, Long appointmentId) {
        String normalizedText = incomingText.trim().toLowerCase();

        if (normalizedText.contains("sim")) {
            log.info("Confirmação recebida para o agendamento {}", appointmentId);
            appointmentService.updateStatus(appointmentId, AppointmentStatus.CONFIRMED);
        } else if (normalizedText.contains("não") || normalizedText.contains("nao")) {
            log.info("Cancelamento recebido para o agendamento {}", appointmentId);
            appointmentService.updateStatus(appointmentId, AppointmentStatus.CANCELED);
        } else {
            log.warn("Mensagem não reconhecida recebida de {}: {}", patientPhone, incomingText);
        }
    }
}
