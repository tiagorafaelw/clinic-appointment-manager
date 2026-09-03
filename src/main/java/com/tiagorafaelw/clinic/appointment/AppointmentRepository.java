package com.tiagorafaelw.clinic.appointment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query("""
        SELECT COUNT(a) > 0
        FROM Appointment a
        WHERE a.professional.id = :professionalId
          AND a.status <> 'CANCELED'
          AND :start < a.endDateTime
          AND :end > a.appointmentDateTime
    """)
    boolean existsOverlappingAppointment(
            @Param("professionalId") Long professionalId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    List<Appointment> findAllByStatusAndAppointmentDateTimeBetween(
            AppointmentStatus status,
            LocalDateTime start,
            LocalDateTime end
    );
}
