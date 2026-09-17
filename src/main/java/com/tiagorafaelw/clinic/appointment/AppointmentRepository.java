package com.tiagorafaelw.clinic.appointment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query("""
            SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END
            FROM Appointment a
            WHERE a.professional.id = :professionalId
              AND a.status <> com.tiagorafaelw.clinic.appointment.AppointmentStatus.CANCELED
              AND a.appointmentDateTime < :end
              AND a.endDateTime > :start
            """)
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
            JOIN FETCH a.patient
            WHERE a.patient.phone = :phone
              AND a.status = com.tiagorafaelw.clinic.appointment.AppointmentStatus.SCHEDULED
            ORDER BY a.appointmentDateTime DESC
            """)
    Optional<Appointment> findLatestScheduledByPatientPhone(String phone);
}
