package co.edu.unbosque.eventos.model;

import org.springframework.http.HttpStatus;

/** Excepción genérica de la API con código HTTP incluido. */
public class ApiException extends RuntimeException {

    private final HttpStatus status;

    public ApiException(HttpStatus status, String mensaje) {
        super(mensaje);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
