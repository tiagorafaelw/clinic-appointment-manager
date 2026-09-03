package com.tiagorafaelw.clinic.appointment;

public enum AppointmentStatus {
    SCHEDULED,   // Agendado
    CONFIRMED,   // Confirmado pelo paciente via WhatsApp
    CANCELED,    // Cancelado
    COMPLETED    // Atendimento realizado
}
