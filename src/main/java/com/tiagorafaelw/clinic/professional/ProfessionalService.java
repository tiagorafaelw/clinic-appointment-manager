package com.tiagorafaelw.clinic.professional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProfessionalService {

    private final ProfessionalRepository repository;

    public ProfessionalService(ProfessionalRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ProfessionalResponse create(ProfessionalRequest request) {
        Professional professional = Professional.builder()
                .name(request.name())
                .specialty(request.specialty())
                .active(true)
                .build();

        return ProfessionalResponse.fromEntity(repository.save(professional));
    }

    @Transactional(readOnly = true)
    public List<ProfessionalResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(ProfessionalResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProfessionalResponse findById(Long id) {
        return repository.findById(id)
                .map(ProfessionalResponse::fromEntity)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Professional not found"));
    }

    @Transactional
    public ProfessionalResponse update(Long id, ProfessionalRequest request) {
        Professional professional = repository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Professional not found"));

        professional.setName(request.name());
        professional.setSpecialty(request.specialty());

        return ProfessionalResponse.fromEntity(repository.save(professional));
    }

    @Transactional
    public void inactivate(Long id) {
        Professional professional = repository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Professional not found"));

        professional.setActive(false);
        repository.save(professional);
    }
}