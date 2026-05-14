package com.security.movil.mapElectric.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private String nombreCompleto;

    @Column(unique = true)
    private String deviceId; // Para control de Trial

    private LocalDateTime fechaRegistro;
    private LocalDateTime fechaExpiracion;

    @Enumerated(EnumType.STRING)
    private Role role;

    public enum Role {
        ROLE_USER,
        ROLE_ADMIN
    }

    // Lógica para saber si el usuario tiene acceso
    public boolean tieneAcceso() {
        return fechaExpiracion != null && LocalDateTime.now().isBefore(fechaExpiracion);
    }

    // Campo dinámico para el frontend (React)
    public String getStatus() {
        if (fechaExpiracion == null) return "EXPIRED";
        if (LocalDateTime.now().isAfter(fechaExpiracion)) return "EXPIRED";
        
        // Si se registró hace menos de 3 días y la expiración es corta, es TRIAL
        if (fechaRegistro != null && fechaRegistro.plusDays(3).isAfter(LocalDateTime.now()) 
            && fechaExpiracion.isBefore(fechaRegistro.plusDays(4))) {
            return "TRIAL";
        }
        
        return "ACTIVE";
    }
}
