package com.tiagorafaelw.clinic.patient;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record PatientRequest(
    @NotBlank(message = "Name is required")
    String name,

    @NotBlank(message = "Phone is required")
    String phone,

    @Email(message = "Email must be valid")
    String email
) {
}
