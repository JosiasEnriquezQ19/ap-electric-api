package com.security.movil.mapElectric.repository;

import com.security.movil.mapElectric.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);

    boolean existsByDeviceId(String deviceId);

    List<Usuario> findByRole(Usuario.Role role);
}
