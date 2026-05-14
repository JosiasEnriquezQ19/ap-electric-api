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

    public Usuario registrar(Usuario usuario) {
        // 1. Encriptar password
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setFechaRegistro(LocalDateTime.now());
        usuario.setRole(Usuario.Role.ROLE_USER);

        // 2. Lógica de los 3 días de prueba (Trial)
        // Verificamos si el dispositivo ya usó la prueba gratis
        if (usuario.getDeviceId() != null && userRepository.existsByDeviceId(usuario.getDeviceId())) {
            // El dispositivo ya está registrado, no le damos días gratis adicionales
            usuario.setFechaExpiracion(LocalDateTime.now());
        } else {
            // Es un dispositivo nuevo, le damos 2 minutos de regalo para pruebas
            usuario.setFechaExpiracion(LocalDateTime.now().plusMinutes(2));
        }

        return userRepository.save(usuario);
    }
}
