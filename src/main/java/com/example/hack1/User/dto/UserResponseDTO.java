package com.example.hack1.User.dto;

import com.example.petworld.dto.Pet.PetSimpleDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

// DTO para la respuesta de usuario
@Getter
@Setter
public class UserResponseDTO {
    private Long id;
    private String username;
    private String email;
    private List<PetSimpleDTO> pets;
}
