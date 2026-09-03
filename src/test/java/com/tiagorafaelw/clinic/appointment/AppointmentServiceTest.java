package com.tiagorafaelw.clinic.appointment;

import com.tiagorafaelw.clinic.patient.Patient;
import com.tiagorafaelw.clinic.patient.PatientRepository;
import com.tiagorafaelw.clinic.procedure.Procedure;
import com.tiagorafaelw.clinic.procedure.ProcedureRepository;
import com.tiagorafaelw.clinic.professional.Professional;
import com.tiagorafaelw.clinic.professional.ProfessionalRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private ProfessionalRepository professionalRepository;

    @Mock
    private ProcedureRepository procedureRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    @Test
    @DisplayName("Deve agendar com sucesso quando todos os dados forem válidos e não houver conflito")
    void shouldCreateAppointmentSuccessfully() {
        // Given
        Long patientId = 1L;
        Long professionalId = 2L;
        Long procedureId = 3L;
        LocalDateTime start = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0);

        Patient patient = Patient.builder().id(patientId).name("João Silva").active(true).build();
        Professional professional = Professional.builder().id(professionalId).name("Dra. Ana").active(true).build();
        Procedure procedure = Procedure.builder().id(procedureId).name("Consulta").durationMinutes(30).price(new BigDecimal("150.00")).active(true).build();

        AppointmentRequest request = new AppointmentRequest(patientId, professionalId, procedureId, start);

        Appointment savedAppointment = Appointment.builder()
                .id(100L)
                .patient(patient)
                .professional(professional)
                .procedure(procedure)
                .dateTime(start)
                .status(AppointmentStatus.SCHEDULED)
                .build();

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(professionalRepository.findById(professionalId)).thenReturn(Optional.of(professional));
        when(procedureRepository.findById(procedureId)).thenReturn(Optional.of(procedure));
        when(appointmentRepository.existsByProfessionalIdAndDateTime(professionalId, start)).thenReturn(false);
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(savedAppointment);

        // When
        AppointmentResponse response = appointmentService.create(request);

        // Then
        assertNotNull(response);
        assertEquals(100L, response.id());
        assertEquals(AppointmentStatus.SCHEDULED, response.status());
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar agendar para profissional com horário já ocupado")
    void shouldThrowExceptionWhenProfessionalHasScheduleConflict() {
        // Given
        Long patientId = 1L;
        Long professionalId = 2L;
        Long procedureId = 3L;
        LocalDateTime start = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0);

        Patient patient = Patient.builder().id(patientId).active(true).build();
        Professional professional = Professional.builder().id(professionalId).active(true).build();
        Procedure procedure = Procedure.builder().id(procedureId).active(true).build();

        AppointmentRequest request = new AppointmentRequest(patientId, professionalId, procedureId, start);

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(professionalRepository.findById(professionalId)).thenReturn(Optional.of(professional));
        when(procedureRepository.findById(procedureId)).thenReturn(Optional.of(procedure));
        when(appointmentRepository.existsByProfessionalIdAndDateTime(professionalId, start)).thenReturn(true);

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            appointmentService.create(request);
        });

        assertTrue(exception.getMessage().contains("conflito") || exception.getMessage().contains("ocupado") || exception.getMessage().contains("conflict"));
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando o paciente estiver inativo ou inexistente")
    void shouldThrowExceptionWhenPatientNotFoundOrInactive() {
        // Given
        Long patientId = 99L;
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        AppointmentRequest request = new AppointmentRequest(patientId, 1L, 1L, start);

        when(patientRepository.findById(patientId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> appointmentService.create(request));
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }
}
