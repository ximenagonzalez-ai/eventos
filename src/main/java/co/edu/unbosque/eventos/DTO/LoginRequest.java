package co.edu.unbosque.eventos.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
 
public class LoginRequest {
 
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no tiene un formato válido")
    private String email;
 
    @NotBlank(message = "La contraseña es obligatoria")
    private String contrasena;
 
    public LoginRequest() {
    }
 
    public String getEmail() {
        return email;
    }
 
    public void setEmail(String email) {
        this.email = email;
    }
 
    public String getContrasena() {
        return contrasena;
    }
 
    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}