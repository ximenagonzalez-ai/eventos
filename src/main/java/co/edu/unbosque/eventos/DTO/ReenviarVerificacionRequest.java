package co.edu.unbosque.eventos.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
 
public class ReenviarVerificacionRequest {
 
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no tiene un formato válido")
    private String email;
 
    public ReenviarVerificacionRequest() {
    }
 
    public String getEmail() {
        return email;
    }
 
    public void setEmail(String email) {
        this.email = email;
    }
}
