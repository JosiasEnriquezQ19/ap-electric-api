package com.security.movil.mapElectric.config;

import com.security.movil.mapElectric.model.Usuario;
import com.security.movil.mapElectric.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Verificar si ya existe un administrador
        if (userRepository.findByUsername("admin").isEmpty()) {
            Usuario admin = Usuario.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .nombreCompleto("Administrador de Sistema")
                    .role(Usuario.Role.ROLE_ADMIN)
                    .fechaRegistro(LocalDateTime.now())
                    .fechaExpiracion(LocalDateTime.now().plusYears(100)) // Acceso permanente
                    .deviceId("SERVER_ADMIN")
                    .build();

            userRepository.save(admin);
            System.out.println("-----------------------------------------");
            System.out.println("USUARIO ADMIN CREADO POR DEFECTO:");
            System.out.println("Usuario: admin");
            System.out.println("Clave: admin123");
            System.out.println("-----------------------------------------");
        }
    }
}
