package com.example.hack1.security.auth.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO para la respuesta después de un login exitoso
 */
@Getter
@Setter
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long userId;
    private String username;
}