package co.edu.unbosque.eventos.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

class ApiValidationExceptionHandlerTest {

    @Test
    void returnsFieldValidationMessageInApiResponse() {
        var bindingResult = new BeanPropertyBindingResult(new Object(), "registroRequestDTO");
        bindingResult.addError(new FieldError(
                "registroRequestDTO",
                "contrasena",
                "npm",
                false,
                null,
                null,
                "La contraseña debe tener entre 8 y 72 caracteres"));

        var response = new ApiValidationExceptionHandler()
                .handleValidationException(new MethodArgumentNotValidException(null, bindingResult));

        assertEquals(400, response.getStatusCode().value());
        assertEquals("DATOS_INVALIDOS", response.getBody().get("codigo"));
        assertEquals(
                "La contraseña debe tener entre 8 y 72 caracteres",
                response.getBody().get("mensaje"));
    }
}
