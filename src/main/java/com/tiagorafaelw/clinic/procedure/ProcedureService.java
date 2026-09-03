package com.tiagorafaelw.clinic.procedure;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProcedureService {

    private final ProcedureRepository repository;

    @Transactional(readOnly = true)
    public List<ProcedureResponse> findAll() {
        return repository.findAllByActiveTrue()
                .stream()
                .map(ProcedureResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProcedureResponse findById(Long id) {
        return repository.findById(id)
                .map(ProcedureResponse::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("Procedimento não encontrado com ID: " + id));
    }

    @Transactional
    public ProcedureResponse create(ProcedureRequest request) {
        Procedure entity = Procedure.builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .durationMinutes(request.durationMinutes())
                .active(true)
                .build();

        return ProcedureResponse.fromEntity(repository.save(entity));
    }

    @Transactional
    public ProcedureResponse update(Long id, ProcedureRequest request) {
        Procedure entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Procedimento não encontrado com ID: " + id));

        entity.setName(request.name());
        entity.setDescription(request.description());
        entity.setPrice(request.price());
        entity.setDurationMinutes(request.durationMinutes());

        return ProcedureResponse.fromEntity(repository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        Procedure entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Procedimento não encontrado com ID: " + id));
        entity.setActive(false);
        repository.save(entity);
    }
}
