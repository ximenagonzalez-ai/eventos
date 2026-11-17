package co.edu.unbosque.eventos.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import org.springframework.stereotype.Service;

/**
 * Servicio para hacer peticiones HTTP GET a APIs externas con java.net.http.HttpClient.
 */
@Service
public class ExternalApiService {

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private static final int RESPONSE_TIMEOUT_SECONDS = 30;

    private static final String USER_AGENT = "EventosUniBosque/1.0 (proyecto academico)";

    public ExternalApiService() {
    }

    /**
     * GET a la URL y devuelve el cuerpo como texto (JSON).
     * Lanza IOException si la respuesta es >= 400.
     */
    public String doGet(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(RESPONSE_TIMEOUT_SECONDS))
                .header("User-Agent", USER_AGENT)
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 400) {
            throw new IOException("Error en la peticion HTTP. Codigo de estado: " + response.statusCode());
        }
        return response.body();
    }
}
