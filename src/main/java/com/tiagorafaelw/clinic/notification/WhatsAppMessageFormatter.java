package com.tiagorafaelw.clinic.notification;

import com.tiagorafaelw.clinic.appointment.Appointment;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class WhatsAppMessageFormatter {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");

    public String buildConfirmationMessage(Appointment appointment) {
        return """
                Olá, %s. Seu horário para %s com %s foi agendado para %s.
                Responda Sim para confirmar ou Não para cancelar.
                """.formatted(
                appointment.getPatient().getName(),
                appointment.getProcedure().getName(),
                appointment.getProfessional().getName(),
                appointment.getAppointmentDateTime().format(DATE_TIME_FORMATTER)
        );
    }
}
