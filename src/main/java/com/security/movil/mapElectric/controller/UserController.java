package com.security.movil.mapElectric.controller;

import com.security.movil.mapElectric.model.Usuario;
import com.security.movil.mapElectric.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PutMapping("/change-password")
    public ResponseEntity<?> cambiarPropiaPassword(Authentication authentication, @RequestBody Map<String, String> request) {
        String currentPassword = request.get("currentPassword");
        String newPassword = request.get("newPassword");

        if (newPassword == null || newPassword.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("La nueva contraseña no puede estar vacía");
        }

        String username = authentication.getName();
        return userRepository.findByUsername(username).map(usuario -> {
            // Verificar que la contraseña actual ingresada coincida con la que está en la base de datos
            if (!passwordEncoder.matches(currentPassword, usuario.getPassword())) {
                return ResponseEntity.badRequest().body("La contraseña actual es incorrecta");
            }

            // Guardar la nueva contraseña cifrada
            usuario.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(usuario);

            return ResponseEntity.ok("Contraseña cambiada con éxito");
        }).orElse(ResponseEntity.status(404).body("Usuario no encontrado"));
    }
}
