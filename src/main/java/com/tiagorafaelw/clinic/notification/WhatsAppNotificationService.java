package com.tiagorafaelw.clinic.notification;

import com.tiagorafaelw.clinic.appointment.Appointment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WhatsAppNotificationService {

    private final WhatsAppMessageFormatter messageFormatter;
    private final EvolutionApiClient evolutionApiClient;

    public void sendAppointmentConfirmation(Appointment appointment) {
        String message = messageFormatter.buildConfirmationMessage(appointment);
        sendMessage(appointment, message);
    }

    public void sendMessage(Appointment appointment, String message) {
        try {
            String patientPhone = appointment.getPatient().getPhone();
            evolutionApiClient.sendTextMessage(patientPhone, message);

            log.info(
                    "Mensagem de WhatsApp enviada para o agendamento {}",
                    appointment.getId()
            );
        } catch (Exception exception) {
            throw new WhatsAppNotificationException(
                    "Não foi possível enviar a mensagem de WhatsApp.",
                    exception
            );
        }
    }
}
