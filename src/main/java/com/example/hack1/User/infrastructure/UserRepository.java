package com.example.hack1.User.infrastructure;


import com.example.hack1.User.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Agregar method para buscar por email
    Optional<User> findByEmail(String email);

    // Verificar si existe un usuario con el email dado
    Boolean existsByEmail(String email);
}
