package com.tiagorafaelw.clinic.procedure;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ProcedureRequest(
        @NotBlank(message = "O nome do procedimento é obrigatório.")
        String name,

        String description,

        @NotNull(message = "O preço é obrigatório.")
        @DecimalMin(value = "0.0", inclusive = false, message = "O preço deve ser maior que zero.")
        BigDecimal price,

        @NotNull(message = "A duração estimada é obrigatória.")
        @Positive(message = "A duração deve ser em minutos positivos.")
        Integer durationMinutes
) {}
