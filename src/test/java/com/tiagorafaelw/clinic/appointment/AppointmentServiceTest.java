package com.tiagorafaelw.clinic.appointment;

import com.tiagorafaelw.clinic.patient.Patient;
import com.tiagorafaelw.clinic.patient.PatientRepository;
import com.tiagorafaelw.clinic.procedure.Procedure;
import com.tiagorafaelw.clinic.procedure.ProcedureRepository;
import com.tiagorafaelw.clinic.professional.Professional;
import com.tiagorafaelw.clinic.professional.ProfessionalRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    private Patient patient;
    private Professional professional;
    private Procedure procedure;
    private LocalDateTime appointmentDateTime;

    @BeforeEach
    void setUp() {
        appointmentDateTime = LocalDateTime.now().plusDays(1).withSecond(0).withNano(0);

        patient = Patient.builder()
                .id(1L)
                .name("Tiago Rafael")
                .phone("41999999999")
                .email("tiago@email.com")
                .active(true)
                .build();

        professional = Professional.builder()
                .id(2L)
                .name("Dra. Ana")
                .specialty("Clínica Geral")
                .active(true)
                .build();

        procedure = Procedure.builder()
                .id(3L)
                .name("Consulta")
                .durationMinutes(60)
                .active(true)
                .build();
    }

    @Test
    void shouldCreateAppointmentWhenThereIsNoScheduleConflict() {
        AppointmentRequest request = new AppointmentRequest(
                patient.getId(),
                professional.getId(),
                procedure.getId(),
                appointmentDateTime,
                "Paciente solicitou confirmação pelo WhatsApp."
        );

        when(patientRepository.findById(patient.getId())).thenReturn(Optional.of(patient));
        when(professionalRepository.findById(professional.getId())).thenReturn(Optional.of(professional));
        when(procedureRepository.findById(procedure.getId())).thenReturn(Optional.of(procedure));
        when(appointmentRepository.existsOverlappingAppointment(
                eq(professional.getId()),
                eq(appointmentDateTime),
                eq(appointmentDateTime.plusMinutes(procedure.getDurationMinutes()))
        )).thenReturn(false);

        when(appointmentRepository.save(any(Appointment.class)))
                .thenAnswer(invocation -> {
                    Appointment appointment = invocation.getArgument(0);
                    appointment.setId(10L);
                    return appointment;
                });

        AppointmentResponse response = appointmentService.create(request);

        ArgumentCaptor<Appointment> appointmentCaptor = ArgumentCaptor.forClass(Appointment.class);
        verify(appointmentRepository).save(appointmentCaptor.capture());

        Appointment savedAppointment = appointmentCaptor.getValue();

        assertEquals(10L, response.id());
        assertEquals(patient.getId(), response.patientId());
        assertEquals(professional.getId(), response.professionalId());
        assertEquals(procedure.getId(), response.procedureId());
        assertEquals(appointmentDateTime, response.appointmentDateTime());
        assertEquals(appointmentDateTime.plusMinutes(60), response.endDateTime());
        assertEquals(AppointmentStatus.SCHEDULED, response.status());
        assertEquals("Paciente solicitou confirmação pelo WhatsApp.", response.notes());

        assertEquals(patient, savedAppointment.getPatient());
        assertEquals(professional, savedAppointment.getProfessional());
        assertEquals(procedure, savedAppointment.getProcedure());
        assertEquals(appointmentDateTime, savedAppointment.getAppointmentDateTime());
        assertEquals(appointmentDateTime.plusMinutes(60), savedAppointment.getEndDateTime());
    }

    @Test
    void shouldThrowExceptionWhenProfessionalHasScheduleConflict() {
        AppointmentRequest request = new AppointmentRequest(
                patient.getId(),
                professional.getId(),
                procedure.getId(),
                appointmentDateTime,
                "Horário em conflito."
        );

        when(patientRepository.findById(patient.getId())).thenReturn(Optional.of(patient));
        when(professionalRepository.findById(professional.getId())).thenReturn(Optional.of(professional));
        when(procedureRepository.findById(procedure.getId())).thenReturn(Optional.of(procedure));
        when(appointmentRepository.existsOverlappingAppointment(
                eq(professional.getId()),
                eq(appointmentDateTime),
                eq(appointmentDateTime.plusMinutes(procedure.getDurationMinutes()))
        )).thenReturn(true);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> appointmentService.create(request)
        );

        assertTrue(exception.getMessage().contains("conflitante"));

        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void shouldThrowExceptionWhenPatientDoesNotExist() {
        AppointmentRequest request = new AppointmentRequest(
                999L,
                professional.getId(),
                procedure.getId(),
                appointmentDateTime,
                null
        );

        when(patientRepository.findById(999L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> appointmentService.create(request)
        );

        assertTrue(exception.getMessage().contains("Paciente não encontrado"));

        verify(professionalRepository, never()).findById(any());
        verify(procedureRepository, never()).findById(any());
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }
}
