package co.edu.unbosque.eventos.controller;

import co.edu.unbosque.eventos.dto.RutaDTO;
import co.edu.unbosque.eventos.service.RutaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rutas")
@Tag(name = "Rutas", description = "Como llegar al evento desde la ubicacion del usuario (OSRM / OpenStreetMap)")
public class RutaController {

    @Autowired
    private RutaService rutaService;

    public RutaController() {
    }

    @Operation(
            summary = "Calcular la ruta hasta el evento",
            description = """
                    Consulta el servidor OSRM de OpenStreetMap y devuelve distancia, duracion, el trazado
                    (lista de puntos [lat, lng] para dibujar en el mapa), los pasos en espanol y un enlace
                    para abrir la misma ruta en Google Maps.

                    El origen es la ubicacion del usuario (la obtiene el navegador con geolocation)
                    y el destino es la latitud/longitud del lugar del evento.
                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ruta calculada"),
            @ApiResponse(responseCode = "400", description = "Coordenadas invalidas"),
            @ApiResponse(responseCode = "404", description = "No existe ruta entre los dos puntos"),
            @ApiResponse(responseCode = "502", description = "El servicio externo de rutas fallo")
    })
    @GetMapping
    public ResponseEntity<?> calcularRuta(
            @Parameter(description = "Latitud del usuario", example = "4.7110") @RequestParam double origenLat,
            @Parameter(description = "Longitud del usuario", example = "-74.0721") @RequestParam double origenLng,
            @Parameter(description = "Latitud del lugar del evento", example = "4.6486") @RequestParam double destinoLat,
            @Parameter(description = "Longitud del lugar del evento", example = "-74.0987") @RequestParam double destinoLng,
            @Parameter(description = "car, foot o bike (por defecto car)", example = "car")
            @RequestParam(defaultValue = "car") String modo) {

        if (!rutaService.coordenadasValidas(origenLat, origenLng)
                || !rutaService.coordenadasValidas(destinoLat, destinoLng)) {
            return respuesta(HttpStatus.BAD_REQUEST, "COORDENADAS_INVALIDAS",
                    "Las coordenadas no son validas (latitud -90..90, longitud -180..180)");
        }

        try {
            Optional<RutaDTO> ruta = rutaService.calcularRuta(origenLat, origenLng, destinoLat, destinoLng, modo);
            if (ruta.isEmpty()) {
                return respuesta(HttpStatus.NOT_FOUND, "RUTA_NO_ENCONTRADA",
                        "No se encontro una ruta entre tu ubicacion y el evento");
            }
            return new ResponseEntity<>(ruta.get(), HttpStatus.OK);
        } catch (Exception e) {
            return respuesta(HttpStatus.BAD_GATEWAY, "SERVICIO_RUTAS_NO_DISPONIBLE",
                    "No se pudo consultar el servicio de rutas: " + e.getMessage());
        }
    }

    @Operation(
            summary = "Enlace de Google Maps hacia el evento",
            description = "Devuelve solo el enlace que abre Google Maps con la ruta ya trazada. No consulta ninguna API externa.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Enlace generado"),
            @ApiResponse(responseCode = "400", description = "Coordenadas invalidas")
    })
    @GetMapping("/enlace-google-maps")
    public ResponseEntity<?> enlaceGoogleMaps(
            @RequestParam double origenLat,
            @RequestParam double origenLng,
            @RequestParam double destinoLat,
            @RequestParam double destinoLng,
            @RequestParam(defaultValue = "car") String modo) {

        if (!rutaService.coordenadasValidas(origenLat, origenLng)
                || !rutaService.coordenadasValidas(destinoLat, destinoLng)) {
            return respuesta(HttpStatus.BAD_REQUEST, "COORDENADAS_INVALIDAS",
                    "Las coordenadas no son validas (latitud -90..90, longitud -180..180)");
        }
        String enlace = rutaService.construirEnlaceGoogleMaps(
                origenLat, origenLng, destinoLat, destinoLng, rutaService.normalizarModo(modo));
        return new ResponseEntity<>(Map.of("enlace", enlace), HttpStatus.OK);
    }

    private ResponseEntity<Map<String, String>> respuesta(HttpStatus status, String codigo, String mensaje) {
        return ResponseEntity.status(status).body(Map.of("codigo", codigo, "mensaje", mensaje));
    }
}
