package com.tiagorafaelw.clinic.appointment;

import com.tiagorafaelw.clinic.patient.Patient;
import com.tiagorafaelw.clinic.patient.PatientRepository;
import com.tiagorafaelw.clinic.procedure.Procedure;
import com.tiagorafaelw.clinic.procedure.ProcedureRepository;
import com.tiagorafaelw.clinic.professional.Professional;
import com.tiagorafaelw.clinic.professional.ProfessionalRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final ProfessionalRepository professionalRepository;
    private final ProcedureRepository procedureRepository;

    @Transactional(readOnly = true)
    public List<AppointmentResponse> findAll() {
        return appointmentRepository.findAll()
                .stream()
                .map(AppointmentResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public AppointmentResponse findById(Long id) {
        return appointmentRepository.findById(id)
                .map(AppointmentResponse::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Agendamento não encontrado com ID: " + id
                ));
    }

    @Transactional
    public AppointmentResponse create(AppointmentRequest request) {
        Patient patient = patientRepository.findById(request.patientId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Paciente não encontrado com ID: " + request.patientId()
                ));

        Professional professional = professionalRepository.findById(request.professionalId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Profissional não encontrado com ID: " + request.professionalId()
                ));

        Procedure procedure = procedureRepository.findById(request.procedureId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Procedimento não encontrado com ID: " + request.procedureId()
                ));

        LocalDateTime start = request.appointmentDateTime();
        LocalDateTime end = start.plusMinutes(procedure.getDurationMinutes());

        boolean hasConflict = appointmentRepository.existsOverlappingAppointment(
                professional.getId(),
                start,
                end
        );

        if (hasConflict) {
            throw new IllegalStateException(
                    "O profissional já possui um agendamento conflitante neste intervalo de horário."
            );
        }

        Appointment appointment = Appointment.builder()
                .patient(patient)
                .professional(professional)
                .procedure(procedure)
                .appointmentDateTime(start)
                .endDateTime(end)
                .status(AppointmentStatus.SCHEDULED)
                .notes(request.notes())
                .build();

        Appointment savedAppointment = appointmentRepository.save(appointment);

        return AppointmentResponse.fromEntity(savedAppointment);
    }

    @Transactional
    public AppointmentResponse updateStatus(Long id, AppointmentStatus newStatus) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Agendamento não encontrado com ID: " + id
                ));

        appointment.setStatus(newStatus);

        return AppointmentResponse.fromEntity(appointmentRepository.save(appointment));
    }
}
