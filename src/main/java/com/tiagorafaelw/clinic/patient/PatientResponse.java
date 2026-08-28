package com.tiagorafaelw.clinic.patient;

import java.time.LocalDateTime;

public record PatientResponse(
    Long id,
    String name,
    String phone,
    String email,
    boolean active,
    LocalDateTime createdAt
) {
    public static PatientResponse fromEntity(Patient patient) {
        return new PatientResponse(
            patient.getId(),
            patient.getName(),
            patient.getPhone(),
            patient.getEmail(),
            patient.isActive(),
            patient.getCreatedAt()
        );
    }
}
