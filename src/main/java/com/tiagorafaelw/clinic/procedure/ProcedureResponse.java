package com.tiagorafaelw.clinic.procedure;

import java.math.BigDecimal;

public record ProcedureResponse(
    Long id,
    String name,
    String description,
    BigDecimal price,
    Integer durationMinutes,
    Boolean active
) {
    public static ProcedureResponse fromEntity(Procedure entity) {
        return new ProcedureResponse(
            entity.getId(),
            entity.getName(),
            entity.getDescription(),
            entity.getPrice(),
            entity.getDurationMinutes(),
            entity.getActive()
        );
    }
}
