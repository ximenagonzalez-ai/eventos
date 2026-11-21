package co.edu.unbosque.eventos.controller;

import java.util.List;
import java.util.Objects;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.eventos.dto.ItinerarioDTO;
import co.edu.unbosque.eventos.model.Usuario;
import co.edu.unbosque.eventos.repository.UsuarioRepository;
import co.edu.unbosque.eventos.service.ItinerarioService;
import co.edu.unbosque.eventos.service.PdfService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/itinerarios")
@Tag(name = "Itinerarios", description = "Gestión de itinerarios personales de eventos por usuario")
@SecurityRequirement(name = "bearerAuth")
public class ItinerarioController {

    private final ItinerarioService itinerarioService;
    private final PdfService pdfService;
    private final UsuarioRepository usuarioRepository;

    public ItinerarioController(
            ItinerarioService itinerarioService,
            PdfService pdfService,
            UsuarioRepository usuarioRepository) {
        this.itinerarioService = itinerarioService;
        this.pdfService = pdfService;
        this.usuarioRepository = usuarioRepository;
    }

    @Operation(summary = "Crear itinerario")
    @PostMapping
    public ItinerarioDTO crear(@RequestBody ItinerarioDTO dto, Authentication authentication) {
        Long usuarioId = esAdministrador(authentication)
                ? dto.getUsuarioId()
                : obtenerUsuarioAutenticadoId(authentication);
        if (usuarioId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El usuario del itinerario es obligatorio.");
        }
        if (!esAdministrador(authentication)
                && dto.getUsuarioId() != null
                && !Objects.equals(dto.getUsuarioId(), usuarioId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes crear itinerarios para otro usuario.");
        }
        return itinerarioService.crear(usuarioId, dto.getNombre());
    }

    @Operation(summary = "Agregar evento al itinerario")
    @PostMapping("/{itinerarioId}/eventos/{eventoId}")
    public ItinerarioDTO agregarEvento(
            @PathVariable Long itinerarioId,
            @PathVariable Long eventoId,
            Authentication authentication) {
        validarAcceso(itinerarioService.obtener(itinerarioId), authentication);
        return itinerarioService.agregarEvento(itinerarioId, eventoId);
    }

    @Operation(summary = "Obtener itinerario por ID")
    @GetMapping("/{itinerarioId}")
    public ItinerarioDTO obtener(@PathVariable Long itinerarioId, Authentication authentication) {
        ItinerarioDTO itinerario = itinerarioService.obtener(itinerarioId);
        validarAcceso(itinerario, authentication);
        return itinerario;
    }

    @Operation(summary = "Listar itinerarios de un usuario")
    @GetMapping("/usuario/{usuarioId}")
    public List<ItinerarioDTO> listarPorUsuario(
            @PathVariable Long usuarioId,
            Authentication authentication) {
        if (!esAdministrador(authentication)
                && !Objects.equals(usuarioId, obtenerUsuarioAutenticadoId(authentication))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes consultar itinerarios de otro usuario.");
        }
        return itinerarioService.listarPorUsuario(usuarioId);
    }

    @Operation(summary = "Descargar itinerario en PDF")
    @GetMapping("/{itinerarioId}/pdf")
    public ResponseEntity<byte[]> descargarPdf(
            @PathVariable Long itinerarioId,
            Authentication authentication) {
        validarAcceso(itinerarioService.obtener(itinerarioId), authentication);
        byte[] pdf = pdfService.generarItinerario(itinerarioId);
        ContentDisposition disposicion = ContentDisposition.attachment()
                .filename("itinerario-" + itinerarioId + ".pdf")
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, disposicion.toString())
                .body(pdf);
    }

    private void validarAcceso(ItinerarioDTO itinerario, Authentication authentication) {
        if (!esAdministrador(authentication)
                && !Objects.equals(itinerario.getUsuarioId(), obtenerUsuarioAutenticadoId(authentication))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes acceso a este itinerario.");
        }
    }

    private boolean esAdministrador(Authentication authentication) {
        return authentication != null && authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMIN"::equals);
    }

    private Long obtenerUsuarioAutenticadoId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Debes iniciar sesión.");
        }
        Usuario usuario = usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "El usuario autenticado ya no existe."));
        return usuario.getId();
    }
}