package com.tiagorafaelw.clinic.appointment;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AppointmentReminderScheduler {

    private static final long ONE_MINUTE_IN_MILLISECONDS = 60_000L;

    private final AppointmentService appointmentService;

    public AppointmentReminderScheduler(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @Scheduled(fixedRate = ONE_MINUTE_IN_MILLISECONDS)
    public void send24HourReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.plusHours(23).minusMinutes(1);
        LocalDateTime end = now.plusHours(24).plusMinutes(1);

        appointmentService.send24HourRemindersBetween(start, end);
    }

    @Scheduled(fixedRate = ONE_MINUTE_IN_MILLISECONDS)
    public void sendOneHourReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.plusMinutes(59);
        LocalDateTime end = now.plusHours(1).plusMinutes(1);

        appointmentService.sendOneHourRemindersBetween(start, end);
    }
}
