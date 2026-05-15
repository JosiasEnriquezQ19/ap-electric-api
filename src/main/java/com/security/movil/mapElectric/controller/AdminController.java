package com.security.movil.mapElectric.controller;

import com.security.movil.mapElectric.model.Usuario;
import com.security.movil.mapElectric.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ROLE_ADMIN')") // Solo el admin puede entrar aquí
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TelegramService telegramService;

    @GetMapping("/users")
    public List<Usuario> listarUsuarios() {
        // Solo listar los que son clientes, no otros administradores
        return userRepository.findByRole(Usuario.Role.ROLE_USER);
    }

    @PutMapping("/users/{id}/activate")
    public ResponseEntity<?> activarSuscripcion(@PathVariable Long id) {
        return userRepository.findById(id).map(usuario -> {
            LocalDateTime fechaActual = LocalDateTime.now();
            
            // Si ya está expirado, empezamos desde hoy. Si no, sumamos a la actual.
            if (usuario.getFechaExpiracion() == null || usuario.getFechaExpiracion().isBefore(fechaActual)) {
                usuario.setFechaExpiracion(fechaActual.plusDays(30));
            } else {
                usuario.setFechaExpiracion(usuario.getFechaExpiracion().plusDays(30));
            }
            
            userRepository.save(usuario);
            
            // Notificar a Telegram
            telegramService.sendMessage("✅ *Suscripción Renovada*\n" +
                    "👤 Usuario: @" + usuario.getUsername() + "\n" +
                    "⏳ Nueva fecha: " + usuario.getFechaExpiracion().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));

            return ResponseEntity.ok("Suscripción renovada por 30 días");
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.ok("Usuario eliminado");
    }
}
