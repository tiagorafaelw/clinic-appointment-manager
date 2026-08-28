package com.tiagorafaelw.clinic.patient;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PatientService {

    private final PatientRepository repository;

    public PatientService(PatientRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public PatientResponse create(PatientRequest request) {
        if (repository.existsByPhone(request.phone())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Phone already registered");
        }

        Patient patient = Patient.builder()
                .name(request.name())
                .phone(request.phone())
                .email(request.email())
                .active(true)
                .build();

        return PatientResponse.fromEntity(repository.save(patient));
    }

    @Transactional(readOnly = true)
    public List<PatientResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(PatientResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public PatientResponse findById(Long id) {
        return repository.findById(id)
                .map(PatientResponse::fromEntity)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found"));
    }

    @Transactional
    public PatientResponse update(Long id, PatientRequest request) {
        Patient patient = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found"));

        repository.findByPhone(request.phone())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Phone already registered to another patient");
                });

        patient.setName(request.name());
        patient.setPhone(request.phone());
        patient.setEmail(request.email());

        return PatientResponse.fromEntity(repository.save(patient));
    }

    @Transactional
    public void inactivate(Long id) {
        Patient patient = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found"));

        patient.setActive(false);
        repository.save(patient);
    }
}
