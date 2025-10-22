package com.example.hack1.security.auth.application;


import com.example.hack1.User.domain.User;
import com.example.hack1.User.dto.JwtResponseDTO;
import com.example.hack1.User.dto.UserAuthDTO;
import com.example.hack1.User.dto.UserCreateDTO;
import com.example.hack1.User.infrastructure.UserRepository;
import com.example.hack1.security.JwtTokenProvider;
import com.example.hack1.security.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * Controlador para manejar la autenticación y registro de usuarios
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    /**
     * Autentica a un usuario y genera un token JWT
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody UserAuthDTO loginRequest) {
        // Autenticar al usuario
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        // Establecer la autenticación en el contexto de seguridad
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generar token JWT
        String jwt = tokenProvider.generateToken(authentication);

        // Obtener detalles del usuario autenticado
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // Crear respuesta con el token JWT
        JwtResponseDTO response = new JwtResponseDTO();
        response.setToken(jwt);
        response.setType("Bearer");
        response.setUserId(userDetails.getId());
        response.setUsername(userDetails.getUsername());

        return ResponseEntity.ok(response);
    }

    /**
     * Registra un nuevo usuario
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserCreateDTO signUpRequest) {
        // Verificar si el email ya existe
        if (userRepository.findByEmail(signUpRequest.getEmail()).isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: El email ya está en uso");
        }

        // Crear nuevo usuario
        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setBornDate(LocalDateTime.now()); // Esto debería venir en el DTO
        user.setCountry("No especificado"); // Esto debería venir en el DTO
        user.setCity("No especificado"); // Esto debería venir en el DTO

        // Guardar usuario en base de datos
        userRepository.save(user);

        return ResponseEntity.ok("Usuario registrado con éxito");
    }
}
