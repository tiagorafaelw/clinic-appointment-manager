package com.tiagorafaelw.clinic.appointment;

import com.tiagorafaelw.clinic.patient.Patient;
import com.tiagorafaelw.clinic.procedure.Procedure;
import com.tiagorafaelw.clinic.professional.Professional;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "professional_id", nullable = false)
    private Professional professional;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "procedure_id", nullable = false)
    private Procedure procedure;

    @Column(name = "appointment_date_time", nullable = false)
    private LocalDateTime appointmentDateTime;

    @Column(name = "end_date_time", nullable = false)
    private LocalDateTime endDateTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AppointmentStatus status;

    @Column(length = 500)
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

        @Column(name = "reminder_24h_sent_at")
    private LocalDateTime reminder24hSentAt;

    @Column(name = "reminder_1h_sent_at")
    private LocalDateTime reminder1hSentAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();

        if (this.status == null) {
            this.status = AppointmentStatus.SCHEDULED;
        }
    }
}
