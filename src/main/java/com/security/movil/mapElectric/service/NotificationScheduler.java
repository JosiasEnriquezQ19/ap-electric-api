package com.security.movil.mapElectric.service;

import com.security.movil.mapElectric.model.Usuario;
import com.security.movil.mapElectric.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class NotificationScheduler {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TelegramService telegramService;

    // Se ejecuta todos los días a las 8:00 AM
    @Scheduled(cron = "0 0 8 * * *")
    public void checkExpirations() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrow = now.plusDays(1);
        
        List<Usuario> users = userRepository.findByRole(Usuario.Role.ROLE_USER);
        
        StringBuilder mensaje = new StringBuilder("📢 *Reporte de Suscripciones - MapElectric*\n\n");
        boolean hayNovedades = false;

        for (Usuario user : users) {
            if (user.getFechaExpiracion() != null) {
                // Vence hoy
                if (user.getFechaExpiracion().isBefore(tomorrow) && user.getFechaExpiracion().isAfter(now)) {
                    mensaje.append("⚠️ *Por vencer hoy:* @").append(user.getUsername()).append("\n");
                    hayNovedades = true;
                }
                // Ya venció
                else if (user.getFechaExpiracion().isBefore(now) && user.getFechaExpiracion().isAfter(now.minusDays(1))) {
                    mensaje.append("🚫 *Venció hace poco:* @").append(user.getUsername()).append("\n");
                    hayNovedades = true;
                }
            }
        }

        if (hayNovedades) {
            telegramService.sendMessage(mensaje.toString());
        }
    }
}
