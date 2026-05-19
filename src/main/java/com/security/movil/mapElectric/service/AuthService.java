package com.security.movil.mapElectric.service;

import com.security.movil.mapElectric.model.Usuario;
import com.security.movil.mapElectric.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TelegramService telegramService;

    public Usuario registrar(Usuario usuario) {
        // 1. Encriptar password
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setFechaRegistro(LocalDateTime.now());
        usuario.setRole(Usuario.Role.ROLE_USER);

        // 2. Lógica de 1 día de prueba (Trial)
        // Verificamos si el dispositivo ya usó la prueba gratis
        if (usuario.getDeviceId() != null && userRepository.existsByDeviceId(usuario.getDeviceId())) {
            // El dispositivo ya está registrado, no le damos días gratis adicionales
            usuario.setFechaExpiracion(LocalDateTime.now());
        } else {
            // Es un dispositivo nuevo, le damos 1 día de regalo
            usuario.setFechaExpiracion(LocalDateTime.now().plusDays(1));
        }

        Usuario saved = userRepository.save(usuario);

        // Notificar a Telegram
        telegramService.sendMessage("🆕 *Nuevo Usuario Registrado*\n" +
                "👤 Usuario: @" + saved.getUsername() + "\n" +
                "📅 Expira: "
                + saved.getFechaExpiracion().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

        return saved;
    }
}
