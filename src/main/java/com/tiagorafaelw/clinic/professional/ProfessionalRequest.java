package com.tiagorafaelw.clinic.professional;

import jakarta.validation.constraints.NotBlank;

public record ProfessionalRequest(
    @NotBlank(message = "Name is required")
    String name,

    @NotBlank(message = "Specialty is required")
    String specialty
) {
}
