package com.tiagorafaelw.clinic.appointment;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record AppointmentRequest(
        @NotNull(message = "ID do paciente é obrigatório.")
        Long patientId,

        @NotNull(message = "ID do profissional é obrigatório.")
        Long professionalId,

        @NotNull(message = "ID do procedimento é obrigatório.")
        Long procedureId,

        @NotNull(message = "Data e hora do agendamento são obrigatórias.")
        @Future(message = "O agendamento deve ser para uma data/hora futura.")
        LocalDateTime appointmentDateTime,

        String notes
) {}
