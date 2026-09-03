package com.tiagorafaelw.clinic.professional;

import java.time.LocalDateTime;

public record ProfessionalResponse(
    Long id,
    String name,
    String specialty,
    boolean active,
    LocalDateTime createdAt
) {
    public static ProfessionalResponse fromEntity(Professional professional) {
        return new ProfessionalResponse(
            professional.getId(),
            professional.getName(),
            professional.getSpecialty(),
            professional.isActive(),
            professional.getCreatedAt()
        );
    }
}