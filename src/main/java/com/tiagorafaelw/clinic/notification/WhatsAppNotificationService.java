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
        String phone = appointment.getPatient().getPhone();
        String message = messageFormatter.buildConfirmationMessage(appointment);

        log.info(
                "Enviando confirmação do agendamento {} para o telefone {}.",
                appointment.getId(),
                phone
        );

        evolutionApiClient.sendTextMessage(phone, message);
    }
}
