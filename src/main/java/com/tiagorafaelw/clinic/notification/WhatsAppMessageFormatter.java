package com.tiagorafaelw.clinic.notification;

import com.tiagorafaelw.clinic.appointment.Appointment;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
public class WhatsAppMessageFormatter {

    private static final DateTimeFormatter FORMATTER = 
            DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm", new Locale("pt", "BR"));

    public String buildConfirmationMessage(Appointment appointment) {
        String patientName = appointment.getPatient().getName();
        String procedureName = appointment.getProcedure().getName();
        String professionalName = appointment.getProfessional().getName();
        String formattedDateTime = appointment.getAppointmentDateTime().format(FORMATTER);

        return String.format(
                "Olá, %s. Seu horário para %s com %s foi agendado para %s.\n" +
                "Responda Sim para confirmar ou Não para cancelar.",
                patientName,
                procedureName,
                professionalName,
                formattedDateTime
        );
    }
}
