package com.tiagorafaelw.clinic.appointment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    boolean existsOverlappingAppointment(
            Long professionalId,
            LocalDateTime start,
            LocalDateTime end
    );

    List<Appointment> findByAppointmentDateTimeBetweenAndStatus(
            LocalDateTime start,
            LocalDateTime end,
            AppointmentStatus status
    );

    @Query("""
            SELECT a
            FROM Appointment a
            WHERE a.patientPhone = :phone
              AND a.status = com.tiagorafaelw.clinic.appointment.AppointmentStatus.SCHEDULED
            ORDER BY a.appointmentDateTime DESC
            """)
    Optional<Appointment> findLatestScheduledByPatientPhone(String phone);
}
