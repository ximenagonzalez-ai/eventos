package co.edu.unbosque.eventos.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.server.ResponseStatusException;

import co.edu.unbosque.eventos.dto.ItinerarioDTO;
import co.edu.unbosque.eventos.model.Usuario;
import co.edu.unbosque.eventos.repository.UsuarioRepository;
import co.edu.unbosque.eventos.service.ItinerarioService;
import co.edu.unbosque.eventos.service.PdfService;

@ExtendWith(MockitoExtension.class)
class ItinerarioControllerTest {

    @Mock
    private ItinerarioService itinerarioService;
    @Mock
    private PdfService pdfService;
    @Mock
    private UsuarioRepository usuarioRepository;

    private ItinerarioController controller;

    @BeforeEach
    void setUp() {
        controller = new ItinerarioController(itinerarioService, pdfService, usuarioRepository);
    }

    @Test
    void ownerCanDownloadItineraryPdf() {
        ItinerarioDTO itinerario = new ItinerarioDTO();
        itinerario.setUsuarioId(12L);
        when(itinerarioService.obtener(7L)).thenReturn(itinerario);
        when(pdfService.generarItinerario(7L)).thenReturn("%PDF-1.4".getBytes());
        when(usuarioRepository.findByEmail("usuario@ejemplo.com"))
                .thenReturn(Optional.of(usuario(12L)));

        var response = controller.descargarPdf(7L, usuarioAutenticado());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_PDF, response.getHeaders().getContentType());
        assertEquals("%PDF-1.4", new String(response.getBody()));
    }

    @Test
    void userCannotDownloadAnotherUsersItinerary() {
        ItinerarioDTO itinerario = new ItinerarioDTO();
        itinerario.setUsuarioId(99L);
        when(itinerarioService.obtener(7L)).thenReturn(itinerario);
        when(usuarioRepository.findByEmail("usuario@ejemplo.com"))
                .thenReturn(Optional.of(usuario(12L)));

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> controller.descargarPdf(7L, usuarioAutenticado()));

        assertEquals(HttpStatus.FORBIDDEN, error.getStatusCode());
        verify(pdfService, never()).generarItinerario(7L);
    }

    @Test
    void userCannotListAnotherUsersItineraries() {
        when(usuarioRepository.findByEmail("usuario@ejemplo.com"))
                .thenReturn(Optional.of(usuario(12L)));

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> controller.listarPorUsuario(99L, usuarioAutenticado()));

        assertEquals(HttpStatus.FORBIDDEN, error.getStatusCode());
        verify(itinerarioService, never()).listarPorUsuario(99L);
    }

    private UsernamePasswordAuthenticationToken usuarioAutenticado() {
        return new UsernamePasswordAuthenticationToken(
                "usuario@ejemplo.com",
                "token",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    private Usuario usuario(Long id) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        return usuario;
    }
}
