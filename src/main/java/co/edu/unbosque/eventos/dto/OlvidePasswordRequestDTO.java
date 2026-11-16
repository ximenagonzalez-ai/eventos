package co.edu.unbosque.eventos.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** DTO para el endpoint POST /api/auth/olvide-password */
public class OlvidePasswordRequestDTO {

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no tiene un formato válido")
    private String email;

    public OlvidePasswordRequestDTO() {}

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
