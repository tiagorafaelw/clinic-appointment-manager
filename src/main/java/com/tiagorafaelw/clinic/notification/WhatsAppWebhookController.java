package com.tiagorafaelw.clinic.notification;

import com.tiagorafaelw.clinic.appointment.AppointmentRepository;
import com.tiagorafaelw.clinic.appointment.AppointmentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/whatsapp")
@RequiredArgsConstructor
public class WhatsAppWebhookController {

    private final AppointmentRepository appointmentRepository;

    @PostMapping("/webhook")
    public ResponseEntity<Void> receiveMessage(
            @RequestParam("From") String from,
            @RequestParam("Body") String body
    ) {
        String reply = body.trim().toLowerCase();
        String phone = from.replace("whatsapp:", "").replaceAll("\\D", "");

        appointmentRepository.findLatestScheduledByPatientPhone(phone).ifPresent(appointment -> {
            if (reply.equals("sim")) {
                appointment.setStatus(AppointmentStatus.CONFIRMED);
                appointmentRepository.save(appointment);
            } else if (reply.equals("não") || reply.equals("nao")) {
                appointment.setStatus(AppointmentStatus.CANCELED);
                appointmentRepository.save(appointment);
            }
        });

        return ResponseEntity.ok().build();
    }
}
