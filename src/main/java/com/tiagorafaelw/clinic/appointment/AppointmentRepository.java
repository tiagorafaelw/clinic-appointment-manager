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
    SELECT a FROM Appointment a
    WHERE a.patient.phone = :phone AND a.status = 'SCHEDULED'
    ORDER BY a.appointmentDateTime DESC
""")
Optional<Appointment> findLatestScheduledByPatientPhone(@Param("phone") String phone);

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
