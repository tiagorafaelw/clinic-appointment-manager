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

    public void sendAppointmentConfirmation(Appointment appointment) {
        String phone = appointment.getPatient().getPhone();
        String message = messageFormatter.buildConfirmationMessage(appointment);

        // Log da mensagem montada pronta para envio
        log.info("Disparando mensagem de confirmação para {}:\n{}", phone, message);

        // TODO: Chamada HTTP para o provedor de WhatsApp (Evolution API / Meta API / Z-API)
    }
}
