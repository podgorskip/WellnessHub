package app.registrationSystem.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthenticationRequest(@NotBlank String username, @NotBlank String password) {
}
