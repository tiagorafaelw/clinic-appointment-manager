package com.tiagorafaelw.clinic.procedure;

import jakarta.validation.constraints.*;
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
