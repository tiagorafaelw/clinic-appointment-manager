package com.tiagorafaelw.clinic.appointment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

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
}
