package com.tiagorafaelw.clinic.appointment;

import java.time.LocalDateTime;

public record AppointmentResponse(
        Long id,
        Long patientId,
        String patientName,
        String patientPhone,
        Long professionalId,
        String professionalName,
        Long procedureId,
        String procedureName,
        LocalDateTime appointmentDateTime,
        LocalDateTime endDateTime,
        AppointmentStatus status,
        String notes
) {
    public static AppointmentResponse fromEntity(Appointment entity) {
        return new AppointmentResponse(
                entity.getId(),
                entity.getPatient().getId(),
                entity.getPatient().getName(),
                entity.getPatient().getPhone(),
                entity.getProfessional().getId(),
                entity.getProfessional().getName(),
                entity.getProcedure().getId(),
                entity.getProcedure().getName(),
                entity.getAppointmentDateTime(),
                entity.getEndDateTime(),
                entity.getStatus(),
                entity.getNotes()
        );
    }
}
