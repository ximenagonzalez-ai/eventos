package co.edu.unbosque.eventos.service;

import co.edu.unbosque.eventos.dto.PasoRutaDTO;
import co.edu.unbosque.eventos.dto.RutaDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Calcula la ruta para llegar a un evento usando OSRM (OpenStreetMap).
 * No lanza excepciones por resultados de negocio: devuelve Optional.empty() cuando no hay ruta
 * y el controller decide el codigo HTTP. Solo propaga excepcion si falla la API externa.
 */
@Service
public class RutaService {

    @Autowired
    private ExternalApiService externalApiService;

    /** URL base del servidor de rutas. */
    @Value("${app.api.routing-url:https://routing.openstreetmap.de}")
    private String routingUrl;

    private final ObjectMapper mapper = new ObjectMapper();

    public RutaService() {
    }

    /** Valida que sean coordenadas reales (latitud -90..90, longitud -180..180). */
    public boolean coordenadasValidas(double lat, double lng) {
        return !Double.isNaN(lat) && !Double.isNaN(lng)
                && lat >= -90 && lat <= 90 && lng >= -180 && lng <= 180;
    }

    /** Devuelve el modo normalizado: "car", "foot" o "bike" (si no lo reconoce, "car"). */
    public String normalizarModo(String modo) {
        if (modo == null) {
            return "car";
        }
        switch (modo.toLowerCase(Locale.ROOT)) {
            case "foot":
            case "walking":
            case "a_pie":
                return "foot";
            case "bike":
            case "bicycle":
            case "bici":
                return "bike";
            default:
                return "car";
        }
    }

    /** Enlace que abre Google Maps con la ruta ya trazada (no requiere API key). */
    public String construirEnlaceGoogleMaps(double origenLat, double origenLng,
                                            double destinoLat, double destinoLng, String modo) {
        String travelMode = "driving";
        if ("foot".equals(modo)) {
            travelMode = "walking";
        } else if ("bike".equals(modo)) {
            travelMode = "bicycling";
        }
        return "https://www.google.com/maps/dir/?api=1"
                + "&origin=" + origenLat + "," + origenLng
                + "&destination=" + destinoLat + "," + destinoLng
                + "&travelmode=" + travelMode;
    }

    /**
     * Consulta OSRM. Optional vacio = no existe ruta entre los dos puntos.
     * Lanza Exception si la API externa falla (timeout, 5xx, JSON invalido).
     */
    public Optional<RutaDTO> calcularRuta(double origenLat, double origenLng,
                                          double destinoLat, double destinoLng,
                                          String modoEntrada) throws Exception {
        String modo = normalizarModo(modoEntrada);

        // OSRM usa el orden longitud,latitud
        String coords = origenLng + "," + origenLat + ";" + destinoLng + "," + destinoLat;
        String url = routingUrl + "/routed-" + modo + "/route/v1/driving/" + coords
                + "?overview=full&geometries=geojson&steps=true&alternatives=false";

        String json = externalApiService.doGet(url);
        JsonNode raiz = mapper.readTree(json);

        if (!"Ok".equals(raiz.path("code").asText())) {
            // "NoRoute", "NoSegment", etc.
            return Optional.empty();
        }
        JsonNode rutas = raiz.path("routes");
        if (!rutas.isArray() || rutas.isEmpty()) {
            return Optional.empty();
        }
        JsonNode ruta = rutas.get(0);

        RutaDTO dto = new RutaDTO();
        dto.setModo(modo);
        dto.setDistanciaMetros(ruta.path("distance").asDouble());
        dto.setDuracionSegundos(ruta.path("duration").asDouble());
        dto.setOrigenLat(origenLat);
        dto.setOrigenLng(origenLng);
        dto.setDestinoLat(destinoLat);
        dto.setDestinoLng(destinoLng);
        dto.setEnlaceGoogleMaps(construirEnlaceGoogleMaps(origenLat, origenLng, destinoLat, destinoLng, modo));

        // GeoJSON viene como [lng, lat]; Leaflet espera [lat, lng]
        List<double[]> trazado = new ArrayList<>();
        for (JsonNode punto : ruta.path("geometry").path("coordinates")) {
            trazado.add(new double[] {punto.get(1).asDouble(), punto.get(0).asDouble()});
        }
        dto.setTrazado(trazado);

        List<PasoRutaDTO> pasos = new ArrayList<>();
        for (JsonNode tramo : ruta.path("legs")) {
            for (JsonNode paso : tramo.path("steps")) {
                String calle = paso.path("name").asText("");
                pasos.add(new PasoRutaDTO(
                        construirInstruccion(paso.path("maneuver"), calle),
                        calle,
                        paso.path("distance").asDouble(),
                        paso.path("duration").asDouble()));
            }
        }
        dto.setPasos(pasos);

        return Optional.of(dto);
    }

    /** OSRM no entrega texto, solo tipo de maniobra + modificador; aqui lo armamos en espanol. */
    private String construirInstruccion(JsonNode maniobra, String calle) {
        String tipo = maniobra.path("type").asText("");
        String modificador = maniobra.path("modifier").asText("");
        String enCalle = calle.isEmpty() ? "" : " en " + calle;

        switch (tipo) {
            case "depart":
                return "Inicia el recorrido" + enCalle;
            case "arrive":
                return "Has llegado a tu destino";
            case "roundabout":
            case "rotary":
            case "roundabout turn":
                return "Entra a la rotonda" + (calle.isEmpty() ? "" : " y sigue por " + calle);
            case "exit roundabout":
            case "exit rotary":
                return "Sal de la rotonda" + enCalle;
            case "merge":
                return "Incorporate" + enCalle;
            case "on ramp":
                return "Toma la entrada" + enCalle;
            case "off ramp":
                return "Toma la salida" + enCalle;
            case "fork":
                return "En la bifurcacion " + traducirModificador(modificador) + enCalle;
            case "end of road":
                return "Al final de la via " + traducirModificador(modificador) + enCalle;
            case "continue":
            case "new name":
                return "Continua " + traducirModificador(modificador) + enCalle;
            default:
                return "Gira " + traducirModificador(modificador) + enCalle;
        }
    }

    private String traducirModificador(String modificador) {
        switch (modificador) {
            case "left":
                return "a la izquierda";
            case "right":
                return "a la derecha";
            case "slight left":
                return "ligeramente a la izquierda";
            case "slight right":
                return "ligeramente a la derecha";
            case "sharp left":
                return "cerrado a la izquierda";
            case "sharp right":
                return "cerrado a la derecha";
            case "uturn":
                return "en U";
            default:
                return "de frente";
        }
    }
}
